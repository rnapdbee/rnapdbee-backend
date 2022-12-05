package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.IdSupplier;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.ValidationComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationPath;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationPath;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationSvgFile;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryParams;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.repository.TertiaryToMultiSecondaryRepository;

import java.util.UUID;


@Service
public class TertiaryToMultiSecondaryService {

    private final TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository;
    private final AnalyzedFileService analyzedFileService;
    private final EngineClient engineClient;
    private final ValidationComponent validationComponent;
    private final ImageComponent imageComponent;

    @Autowired
    private TertiaryToMultiSecondaryService(
            TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository,
            AnalyzedFileService analyzedFileService,
            EngineClient engineClient,
            ValidationComponent validationComponent,
            ImageComponent imageComponent
    ) {
        this.tertiaryToMultiSecondaryRepository = tertiaryToMultiSecondaryRepository;
        this.analyzedFileService = analyzedFileService;
        this.engineClient = engineClient;
        this.validationComponent = validationComponent;
        this.imageComponent = imageComponent;
    }

    public TertiaryToMultiSecondaryMongoEntity analyzeTertiaryToMultiSecondary(
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {

        String filename = validationComponent.validateFilename(contentDispositionHeader);

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
                        )
                );

        tertiaryToMultiSecondaryRepository.save(tertiaryToMultiSecondaryMongoEntity);
        analyzedFileService.saveAnalyzedFile(id, fileContent);

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
}
