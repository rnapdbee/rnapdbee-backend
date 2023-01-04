package pl.poznan.put.rnapdbee.backend.shared.exception;

import java.util.UUID;

public class IdNotFoundException extends RuntimeException {

    public IdNotFoundException(
            String messageFormat,
            UUID id
    ) {
        super(String.format(messageFormat, id));
    }

    public IdNotFoundException(
            String messageFormat,
            String id) {
        super(String.format(messageFormat, id));
    }
}
