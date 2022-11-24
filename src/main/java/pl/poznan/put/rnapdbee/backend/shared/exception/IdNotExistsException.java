package pl.poznan.put.rnapdbee.backend.shared.exception;

public class IdNotExistsException extends RuntimeException {
    public IdNotExistsException(String message) {
        super(message);
    }
}
