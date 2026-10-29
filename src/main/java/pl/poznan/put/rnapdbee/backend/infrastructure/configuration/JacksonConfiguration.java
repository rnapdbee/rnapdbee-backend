package pl.poznan.put.rnapdbee.backend.infrastructure.configuration;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

/**
 * Configures polymorphic type handling for entities stored in MongoDB.
 * <p>
 * {@link pl.poznan.put.rnapdbee.backend.shared.domain.entity.ResultEntity} declares
 * {@code @JsonTypeInfo(use = Id.CLASS)} on {@code Object}-typed fields, which is resolved from the
 * {@code @class} property found in already persisted documents. Jackson 3 rejects all subtypes of
 * {@code java.lang.Object} by default, so a validator scoped to this application's package is
 * registered to keep deserialization of stored results working.
 */
@Configuration
public class JacksonConfiguration {

    static final String APPLICATION_PACKAGE = "pl.poznan.put.rnapdbee.backend";

    @Bean
    public JsonMapperBuilderCustomizer rnapdbeePolymorphicTypeCustomizer() {
        return builder -> builder.polymorphicTypeValidator(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(APPLICATION_PACKAGE)
                        .build());
    }
}
