package pl.poznan.put.rnapdbee.backend.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.ImageInformationByteArray;
import pl.poznan.put.rnapdbee.backend.shared.domain.output2D.Output2D;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Guards the WebClient and JSON wiring. In Spring Boot 4 the webflux starter no longer provides a
 * {@code WebClient.Builder}, and Jackson 3 requires {@code spring.jackson.visibility.creator}
 * to use the private constructors these engine response types rely on.
 */
@SpringBootTest(properties = {"spring.data.mongodb.host=localhost", "spring.data.mongodb.port=27017"})
class EngineWebClientConfigurationTest {

    @Autowired
    @Qualifier("engineWebClient")
    private WebClient engineWebClient;

    @Autowired
    @Qualifier("pdbWebClient")
    private WebClient pdbWebClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bothClientsAreWired() {
        assertNotNull(engineWebClient);
        assertNotNull(pdbWebClient);
    }

    @Test
    void applicationObjectMapperIsJackson3() {
        assertEquals("tools.jackson.databind.json.JsonMapper", objectMapper.getClass().getName());
    }

    @Test
    void engineResponseWithPrivateConstructorsAndLowercaseEnumsDecodes() throws Exception {
        String engineResponse = """
                {"strands":[{"name":"A","sequence":"ACGU","structure":"(())"}],
                 "bpSeq":[],"ct":[],"interactions":[],"structuralElements":null,
                 "imageInformation":{"successfulVisualizationTool":"pseudo_viewer",
                                     "failedVisualizationTool":null,
                                     "drawingResult":"done_by_main_drawer",
                                     "svgFile":"AAAA"}}
                """;

        Output2D<ImageInformationByteArray> output = objectMapper.readValue(engineResponse, Output2D.class);

        assertNotNull(output);
        assertEquals(1, output.getStrands().size());
        assertEquals(VisualizationTool.PSEUDO_VIEWER,
                output.getImageInformation().getSuccessfulVisualizationTool());
    }
}
