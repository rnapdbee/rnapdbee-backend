package pl.poznan.put.rnapdbee.backend.shared;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.shared.domain.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.StructuralElement;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.Output3D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationSvgFile;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMultiEntry;

import java.util.List;

@Component
public class EngineClient {
    private static final String CONTENT_DISPOSITION_HEADER_NAME = "Content-Disposition";
    private static final String REMOVE_ISOLATED_PARAM_NAME = "removeIsolated";
    private static final String STRUCTURAL_ELEMENTS_HANDLING_PARAM_NAME = "structuralElementsHandling";
    private static final String VISUALIZATION_TOOL_PARAM_NAME = "visualizationTool";
    private static final String MODEL_SELECTION_PARAM_NAME = "modelSelection";
    private static final String ANALYSIS_TOOL_PARAM_NAME = "analysisTool";
    private static final String NON_CANONICAL_HANDLING_PARAM_NAME = "nonCanonicalHandling";
    private static final String INCLUDE_NON_CANONICAL_PARAM_NAME = "includeNonCanonical";

    private final WebClient engineWebClient;

    @Value("${rnapdbee.engine.global.multi.path}")
    private String PATH_MULTI;
    @Value("${rnapdbee.engine.global.2d.path}")
    private String PATH_2D;
    @Value("${rnapdbee.engine.global.3d.path}")
    private String PATH_3D;

    @Autowired
    private EngineClient(@Autowired @Qualifier("engineWebClient") WebClient engineWebClient) {
        this.engineWebClient = engineWebClient;
    }

    public EngineResponse2D perform2DAnalysisOnEngine(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {
        return engineWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PATH_2D)
                        .queryParam(REMOVE_ISOLATED_PARAM_NAME, removeIsolated)
                        .queryParam(STRUCTURAL_ELEMENTS_HANDLING_PARAM_NAME, structuralElementsHandling)
                        .queryParam(VISUALIZATION_TOOL_PARAM_NAME, visualizationTool)
                        .build())
                .header(CONTENT_DISPOSITION_HEADER_NAME, contentDispositionHeader)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(EngineResponse2D.class)
                .block();
    }

    public EngineResponse3D perform3DAnalysisOnEngine(
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
                        .path(PATH_3D)
                        .queryParam(MODEL_SELECTION_PARAM_NAME, modelSelection)
                        .queryParam(ANALYSIS_TOOL_PARAM_NAME, analysisTool)
                        .queryParam(NON_CANONICAL_HANDLING_PARAM_NAME, nonCanonicalHandling)
                        .queryParam(REMOVE_ISOLATED_PARAM_NAME, removeIsolated)
                        .queryParam(STRUCTURAL_ELEMENTS_HANDLING_PARAM_NAME, structuralElementsHandling)
                        .queryParam(VISUALIZATION_TOOL_PARAM_NAME, visualizationTool)
                        .build())
                .header(CONTENT_DISPOSITION_HEADER_NAME, contentDispositionHeader)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(EngineResponse3D.class)
                .block();
    }

    public EngineResponseMulti performMultiAnalysisOnEngine(
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool,
            String contentDispositionHeader,
            String fileContent) {
        return engineWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PATH_MULTI)
                        .queryParam(INCLUDE_NON_CANONICAL_PARAM_NAME, includeNonCanonical)
                        .queryParam(REMOVE_ISOLATED_PARAM_NAME, removeIsolated)
                        .queryParam(VISUALIZATION_TOOL_PARAM_NAME, visualizationTool)
                        .build())
                .header(CONTENT_DISPOSITION_HEADER_NAME, contentDispositionHeader)
                .body(BodyInserters.fromValue(fileContent))
                .retrieve()
                .bodyToMono(EngineResponseMulti.class)
                .block();
    }

    private static class EngineResponse2D extends Output2D<ImageInformationByteArray> {
        private EngineResponse2D(
                List<Object> strands,
                List<String> bpSeq,
                List<String> ct,
                List<String> interactions,
                StructuralElement structuralElements,
                ImageInformationByteArray imageInformation) {
            super(strands, bpSeq, ct, interactions, structuralElements, imageInformation);
        }
    }

    private static class EngineResponse3D extends Output3D<ImageInformationByteArray> {
        private EngineResponse3D(
                List<SingleTertiaryModelOutput<ImageInformationByteArray>> models,
                String title) {
            super(models, title);
        }
    }

    private static class EngineResponseMulti extends OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> {
        private EngineResponseMulti(
                List<OutputMultiEntry<ImageInformationByteArray>> entries,
                String title,
                ConsensualVisualizationSvgFile consensualVisualization) {
            super(entries, title, consensualVisualization);
        }
    }
}
