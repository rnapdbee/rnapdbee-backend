package pl.poznan.put.rnapdbee.backend.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import pl.poznan.put.rnapdbee.backend.infrastructure.exception.ExceptionPattern;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.SingleStrand;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.StructuralElement;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import pl.poznan.put.rnapdbee.backend.shared.exception.EngineNotAvailableException;
import pl.poznan.put.rnapdbee.backend.shared.exception.EngineReturnedException;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.Output3D;
import pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain.SingleTertiaryModelOutput;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.ConsensualVisualizationSvgFile;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMulti;
import pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain.OutputMultiEntry;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Engine client class responsible for communication with the rnapdbee-engine service.
 */
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
    private final MessageProvider messageProvider;
    private final Logger logger = LoggerFactory.getLogger(EngineClient.class);

    @Value("${rnapdbee.engine.global.multi.path}")
    private String PATH_MULTI;
    @Value("${rnapdbee.engine.global.2d.path}")
    private String PATH_2D;
    @Value("${rnapdbee.engine.global.3d.path}")
    private String PATH_3D;

    @Autowired
    private EngineClient(
            @Autowired @Qualifier("engineWebClient") WebClient engineWebClient,
            MessageProvider messageProvider
    ) {
        this.engineWebClient = engineWebClient;
        this.messageProvider = messageProvider;
    }

    public Output2D<ImageInformationByteArray> perform2DAnalysisOnEngine(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String filename,
            String fileContent
    ) {
        logger.info("Performing 2D analysis on engine.");
        try {
            return engineWebClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path(PATH_2D)
                            .queryParam(REMOVE_ISOLATED_PARAM_NAME, removeIsolated)
                            .queryParam(STRUCTURAL_ELEMENTS_HANDLING_PARAM_NAME, structuralElementsHandling)
                            .queryParam(VISUALIZATION_TOOL_PARAM_NAME, visualizationTool)
                            .build())
                    .header(CONTENT_DISPOSITION_HEADER_NAME, prepareContentDispositionHeader(filename))
                    .body(BodyInserters.fromValue(fileContent))
                    .retrieve()
                    .onStatus(HttpStatus::isError, clientResponse -> errorHandler(clientResponse, messageProvider))
                    .bodyToMono(EngineResponse2D.class)
                    .block();
        } catch (WebClientRequestException e) {
            logger.error("Calculation engine not available.", e);
            throw new EngineNotAvailableException(
                    messageProvider.getMessage(MessageProvider.Message.ENGINE_NOT_AVAILABLE));
        }
    }

    public Output3D<ImageInformationByteArray> perform3DAnalysisOnEngine(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool,
            String filename,
            String fileContent
    ) {
        logger.info("Performing 3D analysis on engine.");
        try {
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
                    .header(CONTENT_DISPOSITION_HEADER_NAME, prepareContentDispositionHeader(filename))
                    .body(BodyInserters.fromValue(fileContent))
                    .retrieve()
                    .onStatus(HttpStatus::isError, clientResponse -> errorHandler(clientResponse, messageProvider))
                    .bodyToMono(EngineResponse3D.class)
                    .block();
        } catch (WebClientRequestException e) {
            logger.error("Calculation engine not available.", e);
            throw new EngineNotAvailableException(
                    messageProvider.getMessage(MessageProvider.Message.ENGINE_NOT_AVAILABLE));
        }
    }

    public OutputMulti<ImageInformationByteArray, ConsensualVisualizationSvgFile> performMultiAnalysisOnEngine(
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool,
            String filename,
            String fileContent
    ) {
        logger.info("Performing Multi analysis on engine.");
        try {
            return engineWebClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path(PATH_MULTI)
                            .queryParam(INCLUDE_NON_CANONICAL_PARAM_NAME, includeNonCanonical)
                            .queryParam(REMOVE_ISOLATED_PARAM_NAME, removeIsolated)
                            .queryParam(VISUALIZATION_TOOL_PARAM_NAME, visualizationTool)
                            .build())
                    .header(CONTENT_DISPOSITION_HEADER_NAME, prepareContentDispositionHeader(filename))
                    .body(BodyInserters.fromValue(fileContent))
                    .retrieve()
                    .onStatus(HttpStatus::isError, clientResponse -> errorHandler(clientResponse, messageProvider))
                    .bodyToMono(EngineResponseMulti.class)
                    .block();
        } catch (WebClientRequestException e) {
            logger.error("Calculation engine not available.", e);
            throw new EngineNotAvailableException(
                    messageProvider.getMessage(MessageProvider.Message.ENGINE_NOT_AVAILABLE));
        }
    }

    private static Mono<? extends Throwable> errorHandler(
            ClientResponse clientResponse,
            MessageProvider messageProvider
    ) {
        if (clientResponse.statusCode().is5xxServerError()) {
            return Mono.error(new EngineNotAvailableException(
                    messageProvider.getMessage(MessageProvider.Message.ENGINE_NOT_AVAILABLE)));
        }

        return clientResponse.bodyToMono(ExceptionPattern.class)
                .flatMap(exception -> Mono.error(new EngineReturnedException(
                        exception.getMessage(),
                        exception.getStatus(),
                        exception.getError())));
    }

    private String prepareContentDispositionHeader(String filename) {
        return ContentDisposition.builder("attachment")
                .filename(filename)
                .build()
                .toString();
    }

    private static class EngineResponse2D extends Output2D<ImageInformationByteArray> {
        private EngineResponse2D(
                List<SingleStrand> strands,
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
