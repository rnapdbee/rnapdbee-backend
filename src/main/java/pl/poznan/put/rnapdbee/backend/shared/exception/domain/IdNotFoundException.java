package pl.poznan.put.rnapdbee.backend.shared.exception.domain;

import java.util.UUID;

public class IdNotFoundException extends RuntimeException {
    private static final String ID_NOT_FOUND_FORMAT = "Current id '%s' not found.";

    public IdNotFoundException(UUID id) {
        super(String.format(ID_NOT_FOUND_FORMAT, id));
    }
}
