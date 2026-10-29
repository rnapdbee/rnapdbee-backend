package pl.poznan.put.rnapdbee.backend.infrastructure.configuration;

import org.bson.UuidRepresentation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spring Boot 4 moved the MongoDB connection settings from {@code spring.data.mongodb.*} to
 * {@code spring.mongodb.*}. The old names are deprecated and silently ignored, which leaves the
 * driver on its {@code localhost:27017} default so the app cannot reach MongoDB under Docker
 * Compose. These assertions read the values from the shipped properties file (no overrides), so a
 * wrong key name fails the build instead of failing at deploy time.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MongoPropertiesBindingTest {

    @Autowired
    private MongoProperties mongoProperties;

    @Test
    void basePropertiesBind() {
        assertEquals("localhost", mongoProperties.getHost());
        assertEquals(27017, mongoProperties.getPort());
        assertEquals("backend-db", mongoProperties.getDatabase());
        assertEquals("admin", mongoProperties.getAuthenticationDatabase());
        assertEquals("rnapdbee", mongoProperties.getUsername());
        assertEquals(UuidRepresentation.STANDARD, mongoProperties.getRepresentation().getUuid());
    }
}
