package pl.poznan.put.rnapdbee.backend.infrastructure.configuration;

import org.bson.UuidRepresentation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Spring Boot 4 moved the MongoDB connection settings from {@code spring.data.mongodb.*} to
 * {@code spring.mongodb.*}. The old names are deprecated and silently ignored, which leaves the
 * driver on its {@code localhost:27017} default so the app cannot reach MongoDB under Docker
 * Compose.
 * <p>
 * These assertions read the shipped properties file with no overrides, so a wrong key name fails
 * the build instead of the deployment. They deliberately avoid asserting a specific host, because
 * the Docker image builds with {@code -Pdev} and therefore has a different host than a plain
 * build; what matters is that the values are bound rather than left {@code null}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MongoPropertiesBindingTest {

    @Autowired
    private MongoProperties mongoProperties;

    @Test
    void basePropertiesBind() {
        assertNotNull(mongoProperties.getHost(), "spring.mongodb.host must bind");
        assertNotEquals("", mongoProperties.getHost());
        assertEquals(27017, mongoProperties.getPort());
        assertEquals("backend-db", mongoProperties.getDatabase());
        assertEquals("admin", mongoProperties.getAuthenticationDatabase());
        assertEquals("rnapdbee", mongoProperties.getUsername());
        assertEquals(UuidRepresentation.STANDARD, mongoProperties.getRepresentation().getUuid());
    }
}
