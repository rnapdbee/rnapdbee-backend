package pl.poznan.put.rnapdbee.backend.infrastructure.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Spring Framework 6.1+ throws {@code NoResourceFoundException} for unmapped paths. Without an
 * explicit handler the catch-all {@code @ExceptionHandler(Exception.class)} in
 * {@link ApiExceptionHandler} turns those into 500s, which changes the API contract.
 */
@SpringBootTest(
        properties = {"spring.data.mongodb.host=localhost", "spring.data.mongodb.port=27017"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiExceptionHandlerTest {

    @LocalServerPort
    private int port;

    private HttpResponse<String> get(String path) throws Exception {
        return HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void unknownPathReturnsNotFound() throws Exception {
        HttpResponse<String> response = get("/api/v1/engine/download/nonexistent");

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("\"status\":404"));
    }

    @Test
    void unknownRootPathReturnsNotFound() throws Exception {
        assertEquals(404, get("/definitely-not-a-route").statusCode());
    }
}
