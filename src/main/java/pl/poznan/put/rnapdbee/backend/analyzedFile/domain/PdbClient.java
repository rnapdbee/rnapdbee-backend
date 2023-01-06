package pl.poznan.put.rnapdbee.backend.analyzedFile.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbNotAvailableException;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;

/**
 * Class representing Protein Data Bank Http Client
 */
@Component
public class PdbClient {

    private final WebClient pdbWebClient;
    private final MessageProvider messageProvider;
    private static final Logger logger = LoggerFactory.getLogger(PdbClient.class);

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
                        logger.error(String.format("File '%s%s' not found in Protein Data Bank.", pdbId, fileExtension));

                        throw new PdbFileNotFoundException(
                                messageProvider.getMessage(MessageProvider.Message.PDB_FILE_NOT_FOUND_FORMAT), pdbId);
                    })
                    .onStatus(HttpStatus::is5xxServerError, response -> {
                        logger.error("Protein Data Bank not available.");

                        throw new PdbNotAvailableException(
                                messageProvider.getMessage(MessageProvider.Message.PDB_NOT_AVAILABLE));
                    })
                    .bodyToMono(byte[].class)
                    .block();
        } catch (WebClientRequestException e) {
            logger.error("Protein Data Bank not available.", e);

            throw new PdbNotAvailableException(
                    messageProvider.getMessage(MessageProvider.Message.PDB_NOT_AVAILABLE));
        }
    }
}
