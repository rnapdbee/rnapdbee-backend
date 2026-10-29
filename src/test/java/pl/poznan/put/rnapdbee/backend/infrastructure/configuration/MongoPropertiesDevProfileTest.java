package pl.poznan.put.rnapdbee.backend.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checks that the {@code dev} profile still overrides the MongoDB host using the Spring Boot 4
 * property name. Under Docker Compose this is what points the backend at the MongoDB container
 * instead of {@code localhost}.
 */
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MongoPropertiesDevProfileTest {

    @Autowired
    private MongoProperties mongoProperties;

    @Test
    void devProfileOverridesHost() {
        assertEquals("rnapdbee-mongoDB-container", mongoProperties.getHost());
    }
}
