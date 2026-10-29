package pl.poznan.put.rnapdbee.backend.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the springdoc 1.8 -> 3.x upgrade. On Spring Boot 4 the old springdoc 1.x artifacts no
 * longer register any handler mapping, so these endpoints silently return 500 instead of docs.
 */
@SpringBootTest(
        properties = {"spring.mongodb.host=localhost", "spring.mongodb.port=27017"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiConfigurationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext context;

    private HttpResponse<String> get(String path) throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void groupedApiBeansAreRegistered() {
        assertEquals(6, context.getBeanNamesForType(org.springdoc.core.models.GroupedOpenApi.class).length);
    }

    @Test
    void apiDocsAreServed() throws Exception {
        HttpResponse<String> response = get("/v3/api-docs");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"openapi\""), "expected an OpenAPI document");
    }

    @Test
    void groupedApiDocsAndSwaggerUiAreServed() throws Exception {
        assertEquals(200, get("/v3/api-docs/Image").statusCode());
        assertEquals(200, get("/swagger-ui/index.html").statusCode());
    }

    @Test
    void javadocDescriptionsAreIncluded() throws Exception {
        assertTrue(get("/v3/api-docs/Image").body()
                        .contains("Controller class responsible for fetching svg images"),
                "springdoc must still pick up the therapi javadoc descriptions");
    }
}
