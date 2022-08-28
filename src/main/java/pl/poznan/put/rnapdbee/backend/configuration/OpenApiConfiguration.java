package pl.poznan.put.rnapdbee.backend.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI RnapdbeeApi(@Value("${application-version}") String appVersion) {
        Info rnapdbeeBackendApiInfo = new Info()
                .title("Rnapdbee Backend API")
                .version(appVersion);

        return new OpenAPI().info(rnapdbeeBackendApiInfo);
    }

    @Bean
    public GroupedOpenApi calculationControllerApi() {
        return GroupedOpenApi.builder()
                .group("Controller")
                .pathsToMatch("/**")
                .packagesToScan("pl.poznan.put.rnapdbee.backend.controller")
                .build();
    }
}
