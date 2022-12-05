package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.analyzedFile.AnalyzedFileService;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualization;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMultiEntry;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.TertiaryToMultiSecondaryMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.repository.TertiaryToMultiSecondaryRepository;

import javax.servlet.ServletContext;
import java.util.List;

import static pl.poznan.put.rnapdbee.backend.shared.domain.ValidationPolicy.validateFilename;

@Service
public class TertiaryToMultiSecondaryService {

    private final TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository;
    private final AnalyzedFileService analyzedFileService;
    private final WebClient engineWebClient;
    private final ServletContext servletContext;

    @Autowired
    private TertiaryToMultiSecondaryService(
            TertiaryToMultiSecondaryRepository tertiaryToMultiSecondaryRepository,
            AnalyzedFileService analyzedFileService,
            @Autowired @Qualifier("engineWebClient") WebClient engineWebClient,
            ServletContext servletContext
    ) {
        this.tertiaryToMultiSecondaryRepository = tertiaryToMultiSecondaryRepository;
        this.analyzedFileService = analyzedFileService;
        this.engineWebClient = engineWebClient;
        this.servletContext = servletContext;
    }

    public TertiaryToMultiSecondaryMongoEntity analyzeTertiaryToMultiSecondary(
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {

        String filename = validateFilename(contentDispositionHeader);

        EngineResponseMulti engineResponseMulti = performAnalysisOnEngine(
                includeNonCanonical,
                removeIsolated,
                visualizationTool,
                contentDispositionHeader,
                fileContent);


        return null;
    }

    private EngineResponseMulti performAnalysisOnEngine(
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {
        return engineWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/multi")
                        .queryParam("includeNonCanonical", includeNonCanonical)
                        .queryParam("removeIsolated", removeIsolated)
                        .queryParam("visualizationTool", visualizationTool)
                        .build())
                .header("Content-Disposition", contentDispositionHeader)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(EngineResponseMulti.class)
                .block();
    }

    private static class EngineResponseMulti extends OutputMulti<ImageInformationByteArray> {
        private EngineResponseMulti(
                List<OutputMultiEntry<ImageInformationByteArray>> entries,
                String title,
                ConsensualVisualization consensualVisualization) {
            super(entries, title, consensualVisualization);
        }
    }
}
