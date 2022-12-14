package pl.poznan.put.rnapdbee.backend.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PdbWebClientConfiguration {

    private static final ExchangeStrategies EXCHANGE_STRATEGIES = ExchangeStrategies.builder()
            .codecs(ClientCodecConfigurer::defaultCodecs)
            .build();

    @Value("${pdb.provider.global.host}")
    private String pdbProviderUrl;

    @Bean("pdbWebClient")
    public WebClient pdbWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .exchangeStrategies(EXCHANGE_STRATEGIES)
                .baseUrl(pdbProviderUrl)
                .build();
    }
}
