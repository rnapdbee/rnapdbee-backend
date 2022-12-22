package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbFileEntity;
import pl.poznan.put.rnapdbee.backend.shared.BaseAnalyzeService;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.IdNotFoundException;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationPath;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationSvgFile;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryParams;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.repository.TertiaryToMultiSecondaryRepository;

import java.util.Optional;
import java.util.UUID;

import static pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService.PDB_FILE_EXTENSION;

@Service
public class TertiaryToMultiSecondaryService extends BaseAnalyzeService {

    private final TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository;

    @Autowired
    private TertiaryToMultiSecondaryService(
            TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository,
            EngineClient engineClient,
            ImageComponent imageComponent,
            AnalyzedFileService analyzedFileService,
            MessageProvider messageProvider
    ) {
        super(engineClient, imageComponent, analyzedFileService, messageProvider);
        this.tertiaryToMultiSecondaryRepository = tertiaryToMultiSecondaryRepository;
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

        OutputMulti<ImageInformationPath, ConsensualVisualizationPath> outputMulti = saveGraphicsWithPath(
                engineResponseMulti,
                visualizationTool);

        UUID id = IdSupplier.generateId();

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

        tertiaryToMultiSecondaryRepository.save(tertiaryToMultiSecondaryMongoEntity);
        analyzedFileService.saveAnalyzedFile(id, fileContent);

        return tertiaryToMultiSecondaryMongoEntity;
    }

    public TertiaryToMultiSecondaryMongoEntity getResultsTertiaryToMultiSecondary(UUID id) {
        return findTertiaryToMultiSecondaryDocument(id);
    }

    public TertiaryToMultiSecondaryMongoEntity reanalyzeTertiaryToMultiSecondary(
            UUID id,
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool
    ) {
        TertiaryToMultiSecondaryMongoEntity tertiaryToMultiSecondaryMongoEntity = findTertiaryToMultiSecondaryDocument(id);
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
        tertiaryToMultiSecondaryRepository.save(tertiaryToMultiSecondaryMongoEntity);

        return tertiaryToMultiSecondaryMongoEntity;
    }

    public TertiaryToMultiSecondaryMongoEntity analyzePdbTertiaryToMultiSecondary(
            String pdbIdLowercase,
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool
    ) {
        String pdbId = pdbIdLowercase.toUpperCase();

        PdbFileEntity pdbFile = analyzedFileService.fetchPdbStructure(pdbId);
        String filename = pdbId + PDB_FILE_EXTENSION;

        OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> engineResponseMulti =
                engineClient.performMultiAnalysisOnEngine(
                        includeNonCanonical,
                        removeIsolated,
                        visualizationTool,
                        filename,
                        pdbFile.getContent());

        OutputMulti<ImageInformationPath, ConsensualVisualizationPath> outputMulti = saveGraphicsWithPath(
                engineResponseMulti,
                visualizationTool);

        UUID id = IdSupplier.generateId();

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

        tertiaryToMultiSecondaryRepository.save(tertiaryToMultiSecondaryMongoEntity);

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

    private TertiaryToMultiSecondaryMongoEntity findTertiaryToMultiSecondaryDocument(UUID id) {
        Optional<TertiaryToMultiSecondaryMongoEntity> tertiaryToMultiSecondaryMongoEntity =
                tertiaryToMultiSecondaryRepository.findById(id);

        if (tertiaryToMultiSecondaryMongoEntity.isEmpty())
            throw new IdNotFoundException(messageProvider.getMessage("api.exception.id.not.found.format"), id);

        return tertiaryToMultiSecondaryMongoEntity.get();
    }
}
