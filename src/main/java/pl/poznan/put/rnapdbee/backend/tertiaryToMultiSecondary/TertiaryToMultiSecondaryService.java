package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.shared.EngineClient;
import pl.poznan.put.rnapdbee.backend.shared.ImageComponent;
import pl.poznan.put.rnapdbee.backend.shared.ValidationComponent;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.repository.TertiaryToMultiSecondaryRepository;


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

        OutputMulti<ImageInformationByteArray> engineResponseMulti = engineClient.performMultiAnalysisOnEngine(
                includeNonCanonical,
                removeIsolated,
                visualizationTool,
                contentDispositionHeader,
                fileContent);

        return null;
    }
}
