package pl.poznan.put.rnapdbee.backend.analyzedFile.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PdbClient {

    private static final String POSTFIX = ".cif";
    private final WebClient pdbWebClient;

    @Autowired
    private PdbClient(@Autowired @Qualifier("pdbWebClient") WebClient pdbWebClient) {
        this.pdbWebClient = pdbWebClient;
    }

    public String performPdbRequest(String pdbId) {
        return pdbWebClient
                .get()
                .uri(pdbId + POSTFIX)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
