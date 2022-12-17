package pl.poznan.put.rnapdbee.backend.analyzedFile.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbNotAvailableException;

@Component
public class PdbClient {

    private final WebClient pdbWebClient;

    @Autowired
    private PdbClient(@Autowired @Qualifier("pdbWebClient") WebClient pdbWebClient) {
        this.pdbWebClient = pdbWebClient;
    }

    public byte[] performPdbRequest(
            String pdbId,
            String fileExtension) {
        return pdbWebClient
                .get()
                .uri(pdbId + fileExtension)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    throw new PdbFileNotFoundException(pdbId);
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    throw new PdbNotAvailableException();
                })
                .bodyToMono(byte[].class)
                .block();
    }
}
