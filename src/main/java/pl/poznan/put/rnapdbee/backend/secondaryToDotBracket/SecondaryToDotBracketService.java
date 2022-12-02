package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.repository.SecondaryToDotBracketRepository;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageUtils;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.StructuralElement;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.IdNotExistsException;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static pl.poznan.put.rnapdbee.backend.shared.domain.ValidationPolicy.validateFilename;

@Service
public class SecondaryToDotBracketService {
    private final SecondaryToDotBracketRepository secondaryToDotBracketRepository;
    private final AnalyzedFileService analyzedFileService;
    private final WebClient engineWebClient;
    private final ServletContext servletContext;

    @Autowired
    private SecondaryToDotBracketService(
            SecondaryToDotBracketRepository secondaryToDotBracketRepository,
            AnalyzedFileService analyzedFileService,
            @Autowired @Qualifier("engineWebClient") WebClient engineWebClient,
            ServletContext servletContext
    ) {
        this.secondaryToDotBracketRepository = secondaryToDotBracketRepository;
        this.analyzedFileService = analyzedFileService;
        this.engineWebClient = engineWebClient;
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

        SecondaryToDotBracketMongoEntity secondaryToDotBracketMongoEntity =
                SecondaryToDotBracketMongoEntity.of(
                        id,
                        filename,
                        ResultEntity.of(
                                SecondaryToDotBracketParams.of(
                                        removeIsolated,
                                        structuralElementsHandling,
                                        visualizationTool
                                ),
                                Output2D.of(
                                        engineOutput2DResponse,
                                        ImageInformationPath.of(
                                                engineOutput2DResponse.getImageInformation(),
                                                pathToSVGImage)
                                )
                        )
                );

        secondaryToDotBracketRepository.save(secondaryToDotBracketMongoEntity);
        analyzedFileService.saveAnalyzedFile(id, fileContent);

        return secondaryToDotBracketMongoEntity;
    }

    public SecondaryToDotBracketMongoEntity getResultsSecondaryToDotBracket(UUID id) {
        return findSecondaryToDotBracketDocument(id);
    }

    public SecondaryToDotBracketMongoEntity reanalyzeSecondaryToDotBracket(
            UUID id,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool) {

        AnalyzedFileEntity analyzedFile = analyzedFileService.findAnalyzedFile(id);
        SecondaryToDotBracketMongoEntity secondaryToDotBracketMongoEntity = findSecondaryToDotBracketDocument(id);

        String contentDispositionHeader = ContentDisposition.builder("attachment")
                .filename(secondaryToDotBracketMongoEntity.getFilename())
                .build()
                .toString();

        EngineResponse2D engineOutput2DResponse = performAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                analyzedFile.getContent());

        String pathToSVGImage = ImageUtils.generateSvgUrl(
                servletContext,
                engineOutput2DResponse.getImageInformation().getSvgFile()).getRight();

        ResultEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> resultEntity =
                ResultEntity.of(
                        SecondaryToDotBracketParams.of(
                                removeIsolated,
                                structuralElementsHandling,
                                visualizationTool
                        ),
                        Output2D.of(
                                engineOutput2DResponse,
                                ImageInformationPath.of(
                                        engineOutput2DResponse.getImageInformation(),
                                        pathToSVGImage)
                        )
                );

        secondaryToDotBracketMongoEntity.addResult(resultEntity);
        secondaryToDotBracketRepository.save(secondaryToDotBracketMongoEntity);

        return secondaryToDotBracketMongoEntity;
    }

    private SecondaryToDotBracketMongoEntity findSecondaryToDotBracketDocument(UUID id) {
        Optional<SecondaryToDotBracketMongoEntity> secondaryToDotBracketMongoEntity =
                secondaryToDotBracketRepository.findById(id);

        if (secondaryToDotBracketMongoEntity.isEmpty())
            throw new IdNotExistsException(String.format("Current id '%s' not found", id));

        return secondaryToDotBracketMongoEntity.get();
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
                StructuralElement structuralElements,
                ImageInformationByteArray imageInformation) {
            super(strands, bpSeq, ct, interactions, structuralElements, imageInformation);
        }
    }
}
