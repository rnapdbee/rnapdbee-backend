package pl.poznan.put.rnapdbee.backend.infrastructure.configuration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Configuration
public class EngineWebClientConfiguration {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();

    private static final ExchangeStrategies EXCHANGE_STRATEGIES = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs()
                    .jackson2JsonDecoder(new Jackson2JsonDecoder(OBJECT_MAPPER)))
            .build();

    @Value("${rnapdbee.engine.global.host}")
    private String engineCalculationUrl;
    @Value("${rnapdbee.engine.response.timeout.seconds}")
    private Integer engineResponseTimeoutSeconds;

    @Bean("engineWebClient")
    public WebClient engineWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .exchangeStrategies(EXCHANGE_STRATEGIES)
                .baseUrl(engineCalculationUrl)
                .clientConnector(
                        new ReactorClientHttpConnector(HttpClient
                                .create()
                                .responseTimeout(Duration.ofSeconds(engineResponseTimeoutSeconds)))
                )
                .build();
    }
}
