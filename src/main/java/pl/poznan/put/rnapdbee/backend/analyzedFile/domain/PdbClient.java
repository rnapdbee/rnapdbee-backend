package pl.poznan.put.rnapdbee.backend.analyzedFile.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbNotAvailableException;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;

@Component
public class PdbClient {

    private final WebClient pdbWebClient;
    private final MessageProvider messageProvider;

    @Autowired
    private PdbClient(
            @Autowired @Qualifier("pdbWebClient") WebClient pdbWebClient,
            MessageProvider messageProvider
    ) {
        this.pdbWebClient = pdbWebClient;
        this.messageProvider = messageProvider;
    }

    public byte[] performPdbRequest(
            String pdbId,
            String fileExtension) {
        try {
            return pdbWebClient
                    .get()
                    .uri(pdbId + fileExtension)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, response -> {
                        throw new PdbFileNotFoundException(
                                messageProvider.getMessage("api.exception.pdb.file.not.found.format"), pdbId);
                    })
                    .onStatus(HttpStatus::is5xxServerError, response -> {
                        throw new PdbNotAvailableException(
                                messageProvider.getMessage("api.exception.pdb.not.available"));
                    })
                    .bodyToMono(byte[].class)
                    .block();
        } catch (WebClientRequestException e) {
            throw new PdbNotAvailableException(
                    messageProvider.getMessage("api.exception.pdb.not.available"));
        }
    }
}
