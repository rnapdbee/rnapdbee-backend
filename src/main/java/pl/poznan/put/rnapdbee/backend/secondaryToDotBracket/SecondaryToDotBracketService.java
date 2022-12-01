package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.repository.SecondaryToDotBracketRepository;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageUtils;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.AnalyzedFileEntityNotExistException;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.IdNotExistsException;
import pl.poznan.put.rnapdbee.backend.shared.repository.AnalyzedFileRepository;

import javax.servlet.ServletContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static pl.poznan.put.rnapdbee.backend.shared.domain.validateFilename.validateFilename;

@Service
public class SecondaryToDotBracketService {

    private final SecondaryToDotBracketRepository secondaryToDotBracketRepository;
    private final WebClient engineWebClient;
    private final AnalyzedFileRepository analyzedFileRepository;
    private final ServletContext servletContext;

    @Autowired
    private SecondaryToDotBracketService(
            SecondaryToDotBracketRepository secondaryToDotBracketRepository,
            @Autowired @Qualifier("engineWebClient") WebClient engineWebClient,
            AnalyzedFileRepository analyzedFileRepository,
            ServletContext servletContext) {
        this.secondaryToDotBracketRepository = secondaryToDotBracketRepository;
        this.engineWebClient = engineWebClient;
        this.analyzedFileRepository = analyzedFileRepository;
        this.servletContext = servletContext;
    }

    public SecondaryToDotBracketMongoEntity analyzeSecondaryToDotBracket(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {

        String filename = validateFilename(contentDispositionHeader);

        EngineResponse2D engineOutput2DResponse = performAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                fileContent);

        String pathToSVGImage = ImageUtils.generateSvgUrl(
                servletContext,
                engineOutput2DResponse.getImageInformation().getSvgFile()).getRight();

        UUID id = IdSupplier.generateId();

        SecondaryToDotBracketMongoEntity secondaryToDotBracketMongoEntity = buildEntity(
                id,
                engineOutput2DResponse,
                filename,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                pathToSVGImage
        );

        secondaryToDotBracketRepository.save(secondaryToDotBracketMongoEntity);

        analyzedFileRepository.save(
                new AnalyzedFileEntity.Builder()
                        .withId(id)
                        .withContent(fileContent)
                        .build());

        return secondaryToDotBracketMongoEntity;
    }

    public SecondaryToDotBracketMongoEntity getResultSecondaryToDotBracket(UUID id) {
        return findSecondaryToDotBracketDocument(id);
    }

    public SecondaryToDotBracketMongoEntity reanalyzeSecondaryToDotBracket(
            UUID id,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool) {

        Optional<AnalyzedFileEntity> fileContentToAnalyze = analyzedFileRepository.findById(id);
        if (fileContentToAnalyze.isEmpty())
            throw new AnalyzedFileEntityNotExistException(
                    "file to reanalyze not found");

        SecondaryToDotBracketMongoEntity secondaryToDotBracketMongoEntity = findSecondaryToDotBracketDocument(id);

        EngineResponse2D engineOutput2DResponse = performAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                ContentDisposition.builder("attachment")
                        .filename(secondaryToDotBracketMongoEntity.getFilename())
                        .build().toString(),
                fileContentToAnalyze.get().getContent());

        String pathToSVGImage = ImageUtils.generateSvgUrl(
                servletContext,
                engineOutput2DResponse.getImageInformation().getSvgFile()).getRight();

        ResultEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> resultEntity = buildResultsEntity(
                engineOutput2DResponse,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                pathToSVGImage);

        secondaryToDotBracketMongoEntity.addResult(resultEntity);

        secondaryToDotBracketRepository.save(secondaryToDotBracketMongoEntity);

        return secondaryToDotBracketMongoEntity;
    }

    public SecondaryToDotBracketMongoEntity findSecondaryToDotBracketDocument(UUID id) {
        Optional<SecondaryToDotBracketMongoEntity> secondaryToDotBracketMongoEntity =
                secondaryToDotBracketRepository.findById(id);

        if (secondaryToDotBracketMongoEntity.isEmpty())
            throw new IdNotExistsException(String.format("Current id '%s' not found", id));

        return secondaryToDotBracketMongoEntity.get();
    }




    private SecondaryToDotBracketMongoEntity buildEntity(
            UUID id,
            EngineResponse2D engineOutput2DResponse,
            String filename,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String pathToSVGImage) {

        ResultEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> resultsEntity = buildResultsEntity(
                engineOutput2DResponse,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                pathToSVGImage);

        return new SecondaryToDotBracketMongoEntity.Builder()
                .withId(id)
                .withFilename(filename)
                .withResults(new ArrayList<>(List.of(resultsEntity)))
                .withCreatedAt(Instant.now())
                .build();
    }

    private EngineResponse2D performAnalysisOnEngine(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {
        return engineWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/2d")
                        .queryParam("removeIsolated", removeIsolated)
                        .queryParam("structuralElementsHandling", structuralElementsHandling)
                        .queryParam("visualizationTool", visualizationTool)
                        .build())
                .header("Content-Disposition", contentDispositionHeader)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(EngineResponse2D.class)
                .block();
    }

    private static class EngineResponse2D extends Output2D<ImageInformationByteArray> {
        private EngineResponse2D(
                List<Object> strands,
                List<String> bpSeq,
                List<String> ct,
                List<String> interactions,
                Object structuralElements,
                ImageInformationByteArray imageInformation) {
            super(strands, bpSeq, ct, interactions, structuralElements, imageInformation);
        }
    }
}
