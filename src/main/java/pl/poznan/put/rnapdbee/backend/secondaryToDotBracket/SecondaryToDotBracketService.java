package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams;
import pl.poznan.put.rnapdbee.backend.shared.BaseAnalyzeService;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.AnalysisData;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.repository.AnalysisDataRepository;
import pl.poznan.put.rnapdbee.backend.shared.repository.ResultRepository;

import java.util.UUID;

/**
 * Service class responsible for managing secondary to dot bracket scenario analysis
 */
@Service
public class SecondaryToDotBracketService extends BaseAnalyzeService<SecondaryToDotBracketParams, Output2D<ImageInformationPath>, SecondaryToDotBracketMongoEntity> {

    @Autowired
    private SecondaryToDotBracketService(
            EngineClient engineClient,
            ImageComponent imageComponent,
            AnalyzedFileService analyzedFileService,
            MessageProvider messageProvider,
            AnalysisDataRepository<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> analysisDataRepository,
            ResultRepository<SecondaryToDotBracketParams, Output2D<ImageInformationPath>> resultRepository
    ) {
        super(engineClient,
                imageComponent,
                analyzedFileService,
                messageProvider,
                analysisDataRepository,
                resultRepository);
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

        UUID id = IdSupplier.generateId();

        logger.info("Saving analysis results.");
        String pathToSVGImage = saveGraphicWithPath(engineResponse2D, visualizationTool);

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

        saveMongoEntity(secondaryToDotBracketMongoEntity);
        analyzedFileService.saveAnalyzedFile(id, filename, fileContent);

        return secondaryToDotBracketMongoEntity;
    }

    public SecondaryToDotBracketMongoEntity getResultsSecondaryToDotBracket(UUID id) {
        return findDocument(id);
    }

    public SecondaryToDotBracketMongoEntity reanalyzeSecondaryToDotBracket(
            UUID id,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool
    ) {
        String analyzedFile = analyzedFileService.findAnalyzedFile(id);
        SecondaryToDotBracketMongoEntity secondaryToDotBracketMongoEntity = findDocument(id);
        checkDocumentExpiration(secondaryToDotBracketMongoEntity.getCreatedAt(), id);

        Output2D<ImageInformationByteArray> engineResponse2D = engineClient.perform2DAnalysisOnEngine(
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                secondaryToDotBracketMongoEntity.getFilename(),
                analyzedFile);

        logger.info("Saving reanalysis results.");
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
        saveNewResultEntity(id, resultEntity);

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

    @Override
    public SecondaryToDotBracketMongoEntity findDocument(UUID id) {
        AnalysisData analysisData = findAnalysisDataDocument(id);

        SecondaryToDotBracketMongoEntity.Builder entityBuilder =
                new SecondaryToDotBracketMongoEntity.Builder()
                        .withId(analysisData.getId())
                        .withFilename(analysisData.getFilename())
                        .withCreatedAt(analysisData.getCreatedAt())
                        .withUsePdb(analysisData.getUsePdb());

        for (UUID resultId : analysisData.getResults()) {
            entityBuilder.addResult(findResultEntityDocument(resultId, id));
        }

        return entityBuilder.build();
    }
}
