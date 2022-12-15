package pl.poznan.put.rnapdbee.backend.shared.exception.domain;

import java.util.UUID;

public class DocumentExpiredException extends RuntimeException {
    private static final String DOCUMENT_EXPIRED_FORMAT = "Document with id '%s' expired.";

    public DocumentExpiredException(UUID id) {
        super(String.format(DOCUMENT_EXPIRED_FORMAT, id));
    }
}
