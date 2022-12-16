package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.PdbFileEntity;
import pl.poznan.put.rnapdbee.backend.shared.BaseAnalyzeService;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
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

import static pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService.getPdbFileExtension;


@Service
public class TertiaryToMultiSecondaryService extends BaseAnalyzeService {

    private final TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository;
    private final EngineClient engineClient;
    private final ImageComponent imageComponent;

    @Autowired
    private TertiaryToMultiSecondaryService(
            TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository,
            EngineClient engineClient,
            ImageComponent imageComponent,
            AnalyzedFileService analyzedFileService
    ) {
        super(analyzedFileService);
        this.tertiaryToMultiSecondaryRepository = tertiaryToMultiSecondaryRepository;
        this.engineClient = engineClient;
        this.imageComponent = imageComponent;
    }

    public TertiaryToMultiSecondaryMongoEntity analyzeTertiaryToMultiSecondary(
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {

        String filename = validateContentDisposition(contentDispositionHeader);

        OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> engineResponseMulti =
                engineClient.performMultiAnalysisOnEngine(
                        includeNonCanonical,
                        removeIsolated,
                        visualizationTool,
                        contentDispositionHeader,
                        fileContent);

        OutputMulti<ImageInformationPath, ConsensualVisualizationPath> outputMulti = saveGraphicsWithPath(engineResponseMulti);

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

        String contentDispositionHeader = contentDispositionHeaderBuilder(
                tertiaryToMultiSecondaryMongoEntity.getFilename());

        OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> engineResponseMulti =
                engineClient.performMultiAnalysisOnEngine(
                        includeNonCanonical,
                        removeIsolated,
                        visualizationTool,
                        contentDispositionHeader,
                        fileContent);

        OutputMulti<ImageInformationPath, ConsensualVisualizationPath> outputMulti = saveGraphicsWithPath(engineResponseMulti);

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

        PdbFileEntity pdbFile = analyzedFileService.getPdbFile(pdbId);
        String filename = pdbId + getPdbFileExtension();

        String contentDispositionHeader = contentDispositionHeaderBuilder(filename);

        OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> engineResponseMulti =
                engineClient.performMultiAnalysisOnEngine(
                        includeNonCanonical,
                        removeIsolated,
                        visualizationTool,
                        contentDispositionHeader,
                        pdbFile.getContent());

        OutputMulti<ImageInformationPath, ConsensualVisualizationPath> outputMulti = saveGraphicsWithPath(engineResponseMulti);

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
            OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> engineResponseMulti
    ) {
        OutputMulti.Builder<ImageInformationPath, ConsensualVisualizationPath> outputMultiBuilder =
                new OutputMulti.Builder<ImageInformationPath, ConsensualVisualizationPath>()
                        .withTitle(engineResponseMulti.getTitle());

        outputMultiBuilder.withConsensualVisualization(
                ConsensualVisualizationPath.of(
                        imageComponent.generateSvgUrl(
                                engineResponseMulti.getConsensualVisualization().getSvgFile())));

        for (OutputMultiEntry<ImageInformationByteArray> model : engineResponseMulti.getEntries()) {

            String pathToSVGImage = imageComponent.generateSvgUrl(
                    model.getOutput2D().getImageInformation().getSvgFile());

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
            throw new IdNotFoundException(id);

        return tertiaryToMultiSecondaryMongoEntity.get();
    }
}
