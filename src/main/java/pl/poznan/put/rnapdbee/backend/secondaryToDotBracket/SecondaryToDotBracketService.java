package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.EngineOutput2DResponse;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.ImageUtils;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.repository.SecondaryToDotBracketRepository;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationOutput;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.AnalyzedFileEntityNotExistException;
import pl.poznan.put.rnapdbee.backend.shared.exception.FileNameIsNullException;
import pl.poznan.put.rnapdbee.backend.shared.exception.IdNotExistException;
import pl.poznan.put.rnapdbee.backend.shared.repository.AnalyzedFileRepository;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public SecondaryToDotBracketMongoEntity analyseSecondaryToDotBracket(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) throws IOException {


        EngineOutput2DResponse engineOutput2DResponse = engineSecondaryToDotBracketClient(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                fileContent);

        String fileName = validateFileName(contentDispositionHeader);

        String pathToSVGImage = ImageUtils.generateSvgUrl(
                servletContext,
                engineOutput2DResponse.getImageInformation().getSvgFile()).getRight();

        UUID id = IdSupplier.generateId();

        SecondaryToDotBracketMongoEntity secondaryToDotBracketMongoEntity = buildEntity(
                id,
                engineOutput2DResponse,
                fileName,
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
                        .withContentDisposition(contentDispositionHeader)
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
            VisualizationTool visualizationTool) throws IOException {

        Optional<AnalyzedFileEntity> fileContentToAnalyze = analyzedFileRepository.findById(id);
        if (fileContentToAnalyze.isEmpty())
            throw new AnalyzedFileEntityNotExistException(
                    "file to reanalyze not found");

        SecondaryToDotBracketMongoEntity secondaryToDotBracketMongoEntity = findSecondaryToDotBracketDocument(id);

        EngineOutput2DResponse engineOutput2DResponse = engineSecondaryToDotBracketClient(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                fileContentToAnalyze.get().getContentDisposition(),
                fileContentToAnalyze.get().getContent());

        String pathToSVGImage = ImageUtils.generateSvgUrl(
                servletContext,
                engineOutput2DResponse.getImageInformation().getSvgFile()).getRight();

        ResultEntity<SecondaryToDotBracketParams, Output2D> resultEntity = buildResultsEntity(
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
            throw new IdNotExistException(String.format("Current id '%s' not found", id));

        return secondaryToDotBracketMongoEntity.get();
    }

    public String validateFileName(String contentDisposition) {
        String fileName = ContentDisposition.parse(contentDisposition).getFilename();
        if (fileName == null)
            throw new FileNameIsNullException("filename in 'Content-Disposition' header must not be null");

        return fileName;
    }

    public ResultEntity<SecondaryToDotBracketParams, Output2D> buildResultsEntity(
            EngineOutput2DResponse engineOutput2DResponse,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String pathToSVGImage
    ) {
        SecondaryToDotBracketParams secondaryToDotBracketParams =
                new SecondaryToDotBracketParams.Builder()
                        .withRemoveIsolated(removeIsolated)
                        .withStructuralElementsHandling(structuralElementsHandling)
                        .withVisualizationTool(visualizationTool)
                        .build();

        ImageInformationOutput imageInformationOutput =
                new ImageInformationOutput.Builder()
                        .withEngineOutput2DResponse(engineOutput2DResponse.getImageInformation())
                        .withPathToSVGImage(pathToSVGImage)
                        .build();

        Output2D output2D =
                new Output2D.Builder()
                        .withStrands(engineOutput2DResponse.getStrands())
                        .withBpSeq(engineOutput2DResponse.getBpSeq())
                        .withCt(engineOutput2DResponse.getCt())
                        .withInteractions(engineOutput2DResponse.getInteractions())
                        .withStructuralElement(engineOutput2DResponse.getStructuralElements())
                        .withImageInformation(imageInformationOutput)
                        .build();

        return new ResultEntity.Builder<SecondaryToDotBracketParams, Output2D>()
                .withParams(secondaryToDotBracketParams)
                .withOutput(output2D)
                .build();
    }

    public SecondaryToDotBracketMongoEntity buildEntity(
            UUID id,
            EngineOutput2DResponse engineOutput2DResponse,
            String fileName,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String pathToSVGImage) {

        ResultEntity<SecondaryToDotBracketParams, Output2D> resultsEntity = buildResultsEntity(
                engineOutput2DResponse,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                pathToSVGImage);

        return new SecondaryToDotBracketMongoEntity.Builder()
                .withId(id)
                .withFileName(fileName)
                .withResults(new ArrayList<>(List.of(resultsEntity)))
                .withCreatedAt(Instant.now())
                .build();
    }

    public EngineOutput2DResponse engineSecondaryToDotBracketClient(
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
                .bodyToMono(EngineOutput2DResponse.class)
                .block();
    }
}
