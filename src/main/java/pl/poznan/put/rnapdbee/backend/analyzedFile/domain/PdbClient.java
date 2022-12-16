package pl.poznan.put.rnapdbee.backend.analyzedFile.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileNotFoundException;

@Component
public class PdbClient {

    private final WebClient pdbWebClient;

    @Autowired
    private PdbClient(@Autowired @Qualifier("pdbWebClient") WebClient pdbWebClient) {
        this.pdbWebClient = pdbWebClient;
    }

    public String performPdbRequest(String pdbId, String fileExtension) {
        return pdbWebClient
                .get()
                .uri(pdbId + fileExtension)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    throw new PdbFileNotFoundException(pdbId);
                })
                .bodyToMono(String.class)
                .block();
    }
}
