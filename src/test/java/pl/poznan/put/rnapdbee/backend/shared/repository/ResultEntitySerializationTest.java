package pl.poznan.put.rnapdbee.backend.shared.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that results persisted by earlier versions can still be read back after the Jackson 3
 * migration. {@link ResultEntity} resolves its generic fields through {@code @JsonTypeInfo(Id.CLASS)},
 * so the document below is the exact shape stored in MongoDB.
 */
@SpringBootTest(properties = {"spring.mongodb.host=localhost", "spring.mongodb.port=27017"})
class ResultEntitySerializationTest {

    private static final String PERSISTED_RESULT = """
            {
              "id": "11111111-1111-1111-1111-111111111111",
              "params": {
                "@class": "pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams",
                "removeIsolated": true,
                "structuralElementsHandling": "IGNORE_PSEUDOKNOTS",
                "visualizationTool": "PSEUDO_VIEWER"
              },
              "output": null
            }
            """;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void readsPreviouslyPersistedResult() {
        ResultEntity<?, ?> result = objectMapper.readValue(PERSISTED_RESULT, ResultEntity.class);

        assertNotNull(result.getParams());
        assertEquals(
                "pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams",
                result.getParams().getClass().getName());
    }

    @Test
    void writesTheSameShapeAsBefore() {
        ResultEntity<?, ?> result = objectMapper.readValue(PERSISTED_RESULT, ResultEntity.class);

        String rewritten = objectMapper.writeValueAsString(result);

        assertTrue(rewritten.contains(
                        "\"@class\":\"pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams\""),
                "polymorphic type information must be preserved, got: " + rewritten);
    }

    @Test
    void acceptsCaseInsensitiveEnumsAndOmitsNulls() {
        // configured in application.properties via spring.jackson.*
        ResultEntity<?, ?> result = objectMapper.readValue(
                PERSISTED_RESULT.replace("IGNORE_PSEUDOKNOTS", "ignore_pseudoknots"), ResultEntity.class);

        assertNotNull(result.getParams());
        assertTrue(!objectMapper.writeValueAsString(result).contains("\"output\""),
                "null properties should stay omitted (non_null inclusion)");
    }

    @Test
    void rejectsSubtypesOutsideTheApplicationPackage() {
        String payload = PERSISTED_RESULT.replace(
                "pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.SecondaryToDotBracketParams",
                "java.lang.Runtime");

        // the validator limits @class resolution to our own classes, so this must not be instantiated
        assertThrows(JacksonException.class, () -> objectMapper.readValue(payload, ResultEntity.class));
    }
}
