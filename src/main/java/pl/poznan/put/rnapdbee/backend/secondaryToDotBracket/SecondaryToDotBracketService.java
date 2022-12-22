package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.repository.SecondaryToDotBracketRepository;
import pl.poznan.put.rnapdbee.backend.shared.BaseAnalyzeService;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.IdNotFoundException;

import java.util.Optional;
import java.util.UUID;

@Service
public class SecondaryToDotBracketService extends BaseAnalyzeService {

    private final SecondaryToDotBracketRepository secondaryToDotBracketRepository;

    @Autowired
    private SecondaryToDotBracketService(
            SecondaryToDotBracketRepository secondaryToDotBracketRepository,
            EngineClient engineClient,
            ImageComponent imageComponent,
            AnalyzedFileService analyzedFileService,
            MessageProvider messageProvider
    ) {
        super(engineClient, imageComponent, analyzedFileService, messageProvider);
        this.secondaryToDotBracketRepository = secondaryToDotBracketRepository;
    }

    public SecondaryToDotBracketMongoEntity analyzeSecondaryToDotBracket(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String filename,
            String fileContent) {

        Output2D<ImageInformationByteArray> engineResponse2D = engineClient.perform2DAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                filename,
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
        checkDocumentExpiration(secondaryToDotBracketMongoEntity.getCreatedAt(), id);

        Output2D<ImageInformationByteArray> engineResponse2D = engineClient.perform2DAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                secondaryToDotBracketMongoEntity.getFilename(),
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
            throw new IdNotFoundException(messageProvider.getMessage("api.exception.id.not.found.format"), id);

        return secondaryToDotBracketMongoEntity.get();
    }
}
