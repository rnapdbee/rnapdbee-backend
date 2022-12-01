package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.repository.AnalyzedFileRepository;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.TertiaryToDotBracketMongoEntity;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.repository.TertiaryToDotBracketRepository;

import javax.servlet.ServletContext;

import static pl.poznan.put.rnapdbee.backend.shared.domain.validateFilename.validateFilename;

@Service
public class TertiaryToDotBracketService {

    private final TertiaryToDotBracketRepository tertiaryToDotBracketRepository;
    private final WebClient engineWebClient;
    private final AnalyzedFileRepository analyzedFileRepository;
    private final ServletContext servletContext;

    @Autowired
    private TertiaryToDotBracketService(
            TertiaryToDotBracketRepository tertiaryToDotBracketRepository,
            @Autowired @Qualifier("engineWebClient") WebClient engineWebClient,
            AnalyzedFileRepository analyzedFileRepository,
            ServletContext servletContext) {
        this.tertiaryToDotBracketRepository = tertiaryToDotBracketRepository;
        this.engineWebClient = engineWebClient;
        this.analyzedFileRepository = analyzedFileRepository;
        this.servletContext = servletContext;
    }

    public TertiaryToDotBracketMongoEntity analyzeTertiaryToDotBracket(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {

        String filename = validateFilename(contentDispositionHeader);

        Object engineOutput3DResponse = performAnalysisOnEngine(
                modelSelection,
                analysisTool,
                nonCanonicalHandling,
                removeIsolated,
                structuralElementsHandling,
                visualizationTool,
                contentDispositionHeader,
                fileContent);

        return null;
    }

    private Object performAnalysisOnEngine(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {
        return engineWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/3d")
                        .queryParam("modelSelection", modelSelection)
                        .queryParam("analysisTool", analysisTool)
                        .queryParam("nonCanonicalHandling", nonCanonicalHandling)
                        .queryParam("removeIsolated", removeIsolated)
                        .queryParam("structuralElementsHandling", structuralElementsHandling)
                        .queryParam("visualizationTool", visualizationTool)
                        .build())
                .header("Content-Disposition", contentDispositionHeader)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(EngineOutput2DResponse.class)
                .block();
    }
}
