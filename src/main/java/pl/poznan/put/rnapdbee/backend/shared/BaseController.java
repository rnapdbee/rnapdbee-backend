package pl.poznan.put.rnapdbee.backend.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import pl.poznan.put.rnapdbee.backend.shared.exception.FilenameNotSetException;

/**
 * Base controller class.
 */
public abstract class BaseController {

    protected final MessageProvider messageProvider;
    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected BaseController(
            MessageProvider messageProvider
    ) {
        this.messageProvider = messageProvider;
    }

    protected String validateContentDisposition(String contentDisposition) {
        if (contentDisposition == null || contentDisposition.isEmpty()) {
            logger.error("Content-disposition header not set.");

            throw new FilenameNotSetException(
                    messageProvider.getMessage("api.exception.content.disposition.not.set"));
        }

        try {
            String filename = ContentDisposition.parse(contentDisposition).getFilename();
            if (filename == null || filename.isBlank()) {
                logger.error("Filename not set.");

                throw new FilenameNotSetException(
                        messageProvider.getMessage("api.exception.filename.not.set"));
            }
            return filename;

        } catch (IllegalArgumentException exception) {
            logger.error("Filename non-parsable.");

            throw new FilenameNotSetException(
                    messageProvider.getMessage("api.exception.filename.not.parsable"));
        }
    }
}
