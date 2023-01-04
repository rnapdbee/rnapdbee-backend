package pl.poznan.put.rnapdbee.backend.shared;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Component providing messages defined in message.properties file.
 */
@Component
public class MessageProvider {
    protected final MessageSource messageSource;

    protected MessageProvider(
            MessageSource messageSource
    ) {
        this.messageSource = messageSource;
    }

    public String getMessage(Message code) {
        return messageSource.getMessage(
                code.getCode(),
                null,
                LocaleContextHolder.getLocale());
    }

    public enum Message {
        FILE_NOT_FOUND("api.exception.file.not.found"),
        INVALID_PDB_ID_FORMAT("api.exception.invalid.pdb.id.format"),
        PDB_FILE_NOT_FOUND_FORMAT("api.exception.pdb.file.not.found.format"),
        PDB_NOT_AVAILABLE("api.exception.pdb.not.available"),
        PDB_FILE_UNZIP_FORMAT("api.exception.pdb.file.unzip.format"),
        DOCUMENT_EXPIRED_FORMAT("api.exception.document.expired.format"),
        CONTENT_DISPOSITION_NOT_SET("api.exception.content.disposition.not.set"),
        FILENAME_NOT_PARSABLE("api.exception.filename.not.parsable"),
        FILENAME_NOT_SET("api.exception.filename.not.set"),
        ID_NOT_FOUND_FORMAT("api.exception.id.not.found.format"),
        ENGINE_NOT_AVAILABLE("api.exception.engine.not.available"),
        UNKNOWN_ERROR("api.exception.unknown.error");

        private final String messageCode;

        Message(String messageCode) {
            this.messageCode = messageCode;
        }

        String getCode() {
            return this.messageCode;
        }
    }
}
