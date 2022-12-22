package pl.poznan.put.rnapdbee.backend.shared;

import org.slf4j.Logger;
import org.springframework.http.ContentDisposition;
import pl.poznan.put.rnapdbee.backend.shared.exception.FilenameNotSetException;

public abstract class BaseController {

    protected final MessageProvider messageProvider;
    protected final Logger logger;

    protected BaseController(
            MessageProvider messageProvider,
            Logger logger
    ) {
        this.messageProvider = messageProvider;
        this.logger = logger;
    }

    protected String validateContentDisposition(String contentDisposition) {
        if (contentDisposition == null || contentDisposition.isEmpty()) {
            throw new FilenameNotSetException(
                    messageProvider.getMessage("api.exception.content.disposition.not.set"));
        }

        try {
            String filename = ContentDisposition.parse(contentDisposition).getFilename();
            if (filename == null || filename.isEmpty())
                throw new FilenameNotSetException(
                        messageProvider.getMessage("api.exception.filename.not.set"));
            return filename;

        } catch (IllegalArgumentException exception) {
            throw new FilenameNotSetException(
                    messageProvider.getMessage("api.exception.filename.not.parsable"));
        }
    }
}
