package pl.poznan.put.rnapdbee.backend.shared.exception;

import java.util.UUID;

public class DocumentExpiredException extends RuntimeException {

    public DocumentExpiredException(
            String messageFormat,
            UUID id
    ) {
        super(String.format(messageFormat, id));
    }
}
