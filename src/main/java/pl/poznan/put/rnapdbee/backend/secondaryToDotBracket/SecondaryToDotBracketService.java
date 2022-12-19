package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.repository.SecondaryToDotBracketRepository;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.ValidationComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.IdNotFoundException;

import java.util.Optional;
import java.util.UUID;

@Service
public class SecondaryToDotBracketService {
    private final SecondaryToDotBracketRepository secondaryToDotBracketRepository;
    private final AnalyzedFileService analyzedFileService;
    private final EngineClient engineClient;
    private final ValidationComponent validationComponent;
    private final ImageComponent imageComponent;

    @Autowired
    private SecondaryToDotBracketService(
            SecondaryToDotBracketRepository secondaryToDotBracketRepository,
            AnalyzedFileService analyzedFileService,
            EngineClient engineClient,
            ValidationComponent validationComponent,
            ImageComponent imageComponent
    ) {
        this.secondaryToDotBracketRepository = secondaryToDotBracketRepository;
        this.analyzedFileService = analyzedFileService;
        this.engineClient = engineClient;
        this.validationComponent = validationComponent;
        this.imageComponent = imageComponent;
    }

    public SecondaryToDotBracketMongoEntity analyzeSecondaryToDotBracket(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {

        String filename = validationComponent.validateFilename(contentDispositionHeader);

        Output2D<ImageInformationByteArray> engineResponse2D = engineClient.perform2DAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                fileContent);

        String pathToSVGImage = saveGraphicWithPath(engineResponse2D, visualizationTool);

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
                                        engineResponse2D,
                                        ImageInformationPath.of(
                                                engineResponse2D.getImageInformation(),
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

        Output2D<ImageInformationByteArray> engineResponse2D = engineClient.perform2DAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                analyzedFile.getContent());

        String pathToSVGImage = saveGraphicWithPath(engineResponse2D, visualizationTool);

        ResultEntity<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> resultEntity =
                ResultEntity.of(
                        SecondaryToDotBracketParams.of(
                                removeIsolated,
                                structuralElementsHandling,
                                visualizationTool
                        ),
                        Output2D.of(
                                engineResponse2D,
                                ImageInformationPath.of(
                                        engineResponse2D.getImageInformation(),
                                        pathToSVGImage)
                        )
                );

        secondaryToDotBracketMongoEntity.addResult(resultEntity);
        secondaryToDotBracketRepository.save(secondaryToDotBracketMongoEntity);

        return secondaryToDotBracketMongoEntity;
    }

    private String saveGraphicWithPath(
            Output2D<ImageInformationByteArray> engineResponse2D,
            VisualizationTool visualizationTool
    ) {
        if (visualizationTool == VisualizationTool.NONE)
            return null;
        else
            return imageComponent.generateSvgUrl(engineResponse2D.getImageInformation().getSvgFile());
    }

    private SecondaryToDotBracketMongoEntity findSecondaryToDotBracketDocument(UUID id) {
        Optional<SecondaryToDotBracketMongoEntity> secondaryToDotBracketMongoEntity =
                secondaryToDotBracketRepository.findById(id);

        if (secondaryToDotBracketMongoEntity.isEmpty())
            throw new IdNotFoundException(id);

        return secondaryToDotBracketMongoEntity.get();
    }
}
