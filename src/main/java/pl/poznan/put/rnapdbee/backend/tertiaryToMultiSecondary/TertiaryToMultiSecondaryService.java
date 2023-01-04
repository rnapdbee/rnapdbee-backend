package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.images.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.BaseAnalyzeService;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.AnalysisData;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.Scenario;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.repository.AnalysisDataRepository;
import pl.poznan.put.rnapdbee.backend.shared.repository.ResultRepository;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationPath;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationSvgFile;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryParams;

import java.util.UUID;

import static pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService.PDB_FILE_EXTENSION;

/**
 * Service class responsible for managing tertiary to multi secondary scenario analysis
 */
@Service
public class TertiaryToMultiSecondaryService extends BaseAnalyzeService<TertiaryToMultiSecondaryParams, OutputMulti<ImageInformationPath, ConsensualVisualizationPath>, TertiaryToMultiSecondaryMongoEntity> {

    @Autowired
    private TertiaryToMultiSecondaryService(
            EngineClient engineClient,
            ImageComponent imageComponent,
            AnalyzedFileService analyzedFileService,
            MessageProvider messageProvider,
            AnalysisDataRepository analysisDataRepository,
            ResultRepository<TertiaryToMultiSecondaryParams, OutputMulti<ImageInformationPath, ConsensualVisualizationPath>> resultRepository
    ) {
        super(engineClient,
                imageComponent,
                analyzedFileService,
                messageProvider,
                analysisDataRepository,
                resultRepository,
                Scenario.SCENARIO_MULTI);
    }

    public TertiaryToMultiSecondaryMongoEntity analyzeTertiaryToMultiSecondary(
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool,
            String filename,
            String fileContent) {

        OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> engineResponseMulti =
                engineClient.performMultiAnalysisOnEngine(
                        includeNonCanonical,
                        removeIsolated,
                        visualizationTool,
                        filename,
                        fileContent);

        UUID id = IdSupplier.generateId();

        logger.info("Saving analysis results.");
        OutputMulti<ImageInformationPath, ConsensualVisualizationPath> outputMulti = saveGraphicsWithPath(
                engineResponseMulti,
                visualizationTool);

        TertiaryToMultiSecondaryMongoEntity tertiaryToMultiSecondaryMongoEntity =
                TertiaryToMultiSecondaryMongoEntity.of(
                        id,
                        filename,
                        ResultEntity.of(
                                TertiaryToMultiSecondaryParams.of(
                                        includeNonCanonical,
                                        removeIsolated,
                                        visualizationTool
                                ),
                                outputMulti
                        ),
                        false
                );

        saveMongoEntity(tertiaryToMultiSecondaryMongoEntity);
        analyzedFileService.saveAnalyzedFile(id, filename, fileContent);

        return tertiaryToMultiSecondaryMongoEntity;
    }

    public TertiaryToMultiSecondaryMongoEntity getResultsTertiaryToMultiSecondary(UUID id) {
        return findDocument(id);
    }

    public TertiaryToMultiSecondaryMongoEntity reanalyzeTertiaryToMultiSecondary(
            UUID id,
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool
    ) {
        TertiaryToMultiSecondaryMongoEntity tertiaryToMultiSecondaryMongoEntity = findDocument(id);
        checkDocumentExpiration(tertiaryToMultiSecondaryMongoEntity.getCreatedAt(), id);

        String fileContent = getFileContentToReanalyze(
                id,
                tertiaryToMultiSecondaryMongoEntity.getFilename(),
                tertiaryToMultiSecondaryMongoEntity.isUsePdb());

        OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> engineResponseMulti =
                engineClient.performMultiAnalysisOnEngine(
                        includeNonCanonical,
                        removeIsolated,
                        visualizationTool,
                        tertiaryToMultiSecondaryMongoEntity.getFilename(),
                        fileContent);

        logger.info("Saving reanalysis results.");
        OutputMulti<ImageInformationPath, ConsensualVisualizationPath> outputMulti = saveGraphicsWithPath(
                engineResponseMulti,
                visualizationTool);

        ResultEntity<TertiaryToMultiSecondaryParams, OutputMulti<ImageInformationPath, ConsensualVisualizationPath>> resultEntity =
                ResultEntity.of(
                        TertiaryToMultiSecondaryParams.of(
                                includeNonCanonical,
                                removeIsolated,
                                visualizationTool
                        ),
                        outputMulti
                );

        tertiaryToMultiSecondaryMongoEntity.addResult(resultEntity);
        saveNewResultEntity(id, resultEntity);

        return tertiaryToMultiSecondaryMongoEntity;
    }

    public TertiaryToMultiSecondaryMongoEntity analyzePdbTertiaryToMultiSecondary(
            String pdbIdLowercase,
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool
    ) {
        String pdbId = pdbIdLowercase.toUpperCase();

        String pdbFile = analyzedFileService.fetchPdbStructure(pdbId);
        String filename = pdbId + PDB_FILE_EXTENSION;

        OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> engineResponseMulti =
                engineClient.performMultiAnalysisOnEngine(
                        includeNonCanonical,
                        removeIsolated,
                        visualizationTool,
                        filename,
                        pdbFile);

        UUID id = IdSupplier.generateId();

        logger.info("Saving pdb file analysis results.");
        OutputMulti<ImageInformationPath, ConsensualVisualizationPath> outputMulti = saveGraphicsWithPath(
                engineResponseMulti,
                visualizationTool);

        TertiaryToMultiSecondaryMongoEntity tertiaryToMultiSecondaryMongoEntity =
                TertiaryToMultiSecondaryMongoEntity.of(
                        id,
                        filename,
                        ResultEntity.of(
                                TertiaryToMultiSecondaryParams.of(
                                        includeNonCanonical,
                                        removeIsolated,
                                        visualizationTool
                                ),
                                outputMulti
                        ),
                        true
                );

        saveMongoEntity(tertiaryToMultiSecondaryMongoEntity);

        return tertiaryToMultiSecondaryMongoEntity;
    }

    private OutputMulti<ImageInformationPath, ConsensualVisualizationPath> saveGraphicsWithPath(
            OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> engineResponseMulti,
            VisualizationTool visualizationTool
    ) {
        OutputMulti.Builder<ImageInformationPath, ConsensualVisualizationPath> outputMultiBuilder =
                new OutputMulti.Builder<ImageInformationPath, ConsensualVisualizationPath>()
                        .withTitle(engineResponseMulti.getTitle());

        outputMultiBuilder.withConsensualVisualization(
                ConsensualVisualizationPath.of(
                        imageComponent.generateSvgUrl(
                                engineResponseMulti.getConsensualVisualization().getSvgFile())));

        for (OutputMultiEntry<ImageInformationByteArray> model : engineResponseMulti.getEntries()) {
            String pathToSVGImage;

            if (visualizationTool == VisualizationTool.NONE)
                pathToSVGImage = null;
            else
                pathToSVGImage = imageComponent.generateSvgUrl(model.getOutput2D().getImageInformation().getSvgFile());

            outputMultiBuilder.addEntry(
                    OutputMultiEntry.of(
                            Output2D.of(
                                    model.getOutput2D(),
                                    ImageInformationPath.of(
                                            model.getOutput2D().getImageInformation(),
                                            pathToSVGImage
                                    )
                            ),
                            model.getAdapterEnums()
                    )
            );
        }

        return outputMultiBuilder.build();
    }

    @Override
    public TertiaryToMultiSecondaryMongoEntity findDocument(UUID id) {
        AnalysisData analysisData = findAnalysisDataDocument(id);

        TertiaryToMultiSecondaryMongoEntity.Builder entityBuilder =
                new TertiaryToMultiSecondaryMongoEntity.Builder()
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
