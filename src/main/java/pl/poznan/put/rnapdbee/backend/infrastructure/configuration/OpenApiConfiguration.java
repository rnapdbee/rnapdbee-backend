package pl.poznan.put.rnapdbee.backend.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI rnapdbeeApi(@Value("${application-version}") String appVersion) {
        Info rnapdbeeBackendApiInfo = new Info()
                .title("Rnapdbee Backend API")
                .version(appVersion);

        return new OpenAPI().info(rnapdbeeBackendApiInfo);
    }

    @Bean
    public GroupedOpenApi tertiaryToDotBracketApi() {
        return GroupedOpenApi.builder()
                .group("TertiaryToDotBracket")
                .pathsToMatch("/**")
                .packagesToScan("pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket")
                .build();
    }

    @Bean
    public GroupedOpenApi secondaryToDotBracketApi() {
        return GroupedOpenApi.builder()
                .group("SecondaryToDotBracket")
                .pathsToMatch("/**")
                .packagesToScan("pl.poznan.put.rnapdbee.backend.secondaryToDotBracket")
                .build();
    }

    @Bean
    public GroupedOpenApi tertiaryToMultiSecondaryApi() {
        return GroupedOpenApi.builder()
                .group("TertiaryToMultiSecondary")
                .pathsToMatch("/**")
                .packagesToScan("pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary")
                .build();
    }

    @Bean
    public GroupedOpenApi downloadResultApi() {
        return GroupedOpenApi.builder()
                .group("Download")
                .pathsToMatch("/**")
                .packagesToScan("pl.poznan.put.rnapdbee.backend.downloadResult")
                .build();
    }
}
