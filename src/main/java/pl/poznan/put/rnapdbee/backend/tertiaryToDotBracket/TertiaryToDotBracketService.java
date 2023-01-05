package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket;

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
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.repository.AnalysisDataRepository;
import pl.poznan.put.rnapdbee.backend.shared.repository.ResultRepository;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.Output3D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketParams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService.PDB_FILE_EXTENSION;

/**
 * Service class responsible for managing tertiary to dot bracket scenario analysis
 */
@Service
public class TertiaryToDotBracketService extends BaseAnalyzeService<TertiaryToDotBracketParams, Output3D<ImageInformationPath>, TertiaryToDotBracketMongoEntity> {

    @Autowired
    private TertiaryToDotBracketService(
            EngineClient engineClient,
            ImageComponent imageComponent,
            AnalyzedFileService analyzedFileService,
            MessageProvider messageProvider,
            AnalysisDataRepository analysisDataRepository,
            ResultRepository<TertiaryToDotBracketParams, Output3D<ImageInformationPath>> resultRepository
    ) {
        super(engineClient,
                imageComponent,
                analyzedFileService,
                messageProvider,
                analysisDataRepository,
                resultRepository,
                Scenario.SCENARIO_3D);
    }

    public TertiaryToDotBracketMongoEntity analyzeTertiaryToDotBracket(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String filename,
            String fileContent) {

        Output3D<ImageInformationByteArray> engineResponse3D = engineClient.perform3DAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                filename,
                fileContent);

        UUID id = IdSupplier.generateId();

        logger.info("Saving analysis results.");
        Output3D<ImageInformationPath> output3D = saveGraphicsWithPath(
                engineResponse3D,
                visualizationTool);

        TertiaryToDotBracketMongoEntity tertiaryToDotBracketMongoEntity =
                TertiaryToDotBracketMongoEntity.of(
                        id,
                        filename,
                        ResultEntity.of(
                                TertiaryToDotBracketParams.of(
                                        modelSelection,
                                        analysisTool,
                                        nonCanonicalHandling,
                                        removeIsolated,
                                        structuralElementsHandling,
                                        visualizationTool
                                ),
                                output3D
                        ),
                        false
                );

        saveMongoEntity(tertiaryToDotBracketMongoEntity);
        analyzedFileService.saveAnalyzedFile(id, filename, fileContent);

        return tertiaryToDotBracketMongoEntity;
    }

    public TertiaryToDotBracketMongoEntity getResultsTertiaryToDotBracket(UUID id) {
        return findDocument(id);
    }

    public TertiaryToDotBracketMongoEntity reanalyzeTertiaryToDotBracket(
            UUID id,
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool
    ) {
        TertiaryToDotBracketMongoEntity tertiaryToDotBracketMongoEntity = findDocument(id);
        checkDocumentExpiration(tertiaryToDotBracketMongoEntity.getCreatedAt(), id);

        String fileContent = getFileContentToReanalyze(
                id,
                tertiaryToDotBracketMongoEntity.getFilename(),
                tertiaryToDotBracketMongoEntity.isUsePdb());

        Output3D<ImageInformationByteArray> engineResponse3D = engineClient.perform3DAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                tertiaryToDotBracketMongoEntity.getFilename(),
                fileContent);

        logger.info("Saving reanalysis results.");
        Output3D<ImageInformationPath> output3D = saveGraphicsWithPath(
                engineResponse3D,
                visualizationTool);

        ResultEntity<TertiaryToDotBracketParams, Output3D<ImageInformationPath>> resultEntity =
                ResultEntity.of(
                        TertiaryToDotBracketParams.of(
                                modelSelection,
                                analysisTool,
                                nonCanonicalHandling,
                                removeIsolated,
                                structuralElementsHandling,
                                visualizationTool
                        ),
                        output3D
                );

        tertiaryToDotBracketMongoEntity.addResult(resultEntity);
        saveNewResultEntity(id, resultEntity);

        return tertiaryToDotBracketMongoEntity;
    }

    public TertiaryToDotBracketMongoEntity analyzePdbTertiaryToDotBracket(
            String pdbIdLowercase,
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool
    ) {
        String pdbId = pdbIdLowercase.toUpperCase();

        String pdbFile = analyzedFileService.fetchPdbStructure(pdbId);
        String filename = pdbId + PDB_FILE_EXTENSION;

        Output3D<ImageInformationByteArray> engineResponse3D = engineClient.perform3DAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                filename,
                pdbFile);

        UUID id = IdSupplier.generateId();

        logger.info("Saving pdb file analysis results.");
        Output3D<ImageInformationPath> output3D = saveGraphicsWithPath(
                engineResponse3D,
                visualizationTool);

        TertiaryToDotBracketMongoEntity tertiaryToDotBracketMongoEntity =
                TertiaryToDotBracketMongoEntity.of(
                        id,
                        filename,
                        ResultEntity.of(
                                TertiaryToDotBracketParams.of(
                                        modelSelection,
                                        analysisTool,
                                        nonCanonicalHandling,
                                        removeIsolated,
                                        structuralElementsHandling,
                                        visualizationTool
                                ),
                                output3D
                        ),
                        true
                );

        saveMongoEntity(tertiaryToDotBracketMongoEntity);

        return tertiaryToDotBracketMongoEntity;
    }

    private Output3D<ImageInformationPath> saveGraphicsWithPath(
            Output3D<ImageInformationByteArray> engineResponse3D,
            VisualizationTool visualizationTool
    ) {
        Output3D.Builder<ImageInformationPath> output3DBuilder =
                new Output3D.Builder<ImageInformationPath>()
                        .withTitle(engineResponse3D.getTitle());

        for (SingleTertiaryModelOutput<ImageInformationByteArray> model : engineResponse3D.getModels()) {
            String pathToSVGImage;

            if (visualizationTool == VisualizationTool.NONE)
                pathToSVGImage = null;
            else
                pathToSVGImage = imageComponent.generateSvgUrl(model.getOutput2D().getImageInformation().getSvgFile());

            output3DBuilder.addModel(
                    SingleTertiaryModelOutput.of(
                            model,
                            Output2D.of(
                                    model.getOutput2D(),
                                    ImageInformationPath.of(
                                            model.getOutput2D().getImageInformation(),
                                            pathToSVGImage
                                    )
                            )
                    )
            );
        }

        return output3DBuilder.build();
    }

    @Override
    public TertiaryToDotBracketMongoEntity findDocument(UUID id) {
        AnalysisData analysisData = findAnalysisDataDocument(id);

        TertiaryToDotBracketMongoEntity.Builder entityBuilder =
                new TertiaryToDotBracketMongoEntity.Builder()
                        .withId(analysisData.getId())
                        .withFilename(analysisData.getFilename())
                        .withCreatedAt(analysisData.getCreatedAt())
                        .withUsePdb(analysisData.getUsePdb());

        for (UUID resultId : analysisData.getResults()) {
            entityBuilder.addResult(findResultEntityDocument(resultId, id));
        }

        return entityBuilder.build();
    }

    @Override
    public void deleteExpiredResults(List<UUID> expiredResultsIds) {
        for (UUID expiredResultId : expiredResultsIds) {
            Optional<ResultEntity<TertiaryToDotBracketParams, Output3D<ImageInformationPath>>> optionalResultEntity =
                    findExpiredResultEntityDocument(expiredResultId);

            if (optionalResultEntity.isEmpty() || isEmptyVisualization(optionalResultEntity.get()))
                continue;

            optionalResultEntity.get()
                    .getOutput()
                    .getModels()
                    .stream()
                    .map(model -> model.getOutput2D().getImageInformation().getPathToSVGImage())
                    .forEach(imageComponent::deleteSvgImage);
        }

        resultRepository.deleteAllById(expiredResultsIds);
    }

    @Override
    protected boolean isEmptyVisualization(ResultEntity<TertiaryToDotBracketParams, Output3D<ImageInformationPath>> resultEntity) {
        return resultEntity.getParams().getVisualizationTool() == VisualizationTool.NONE;
    }
}
