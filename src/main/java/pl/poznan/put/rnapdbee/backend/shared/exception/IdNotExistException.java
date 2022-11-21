package pl.poznan.put.rnapdbee.backend.shared.exception;

public class IdNotExistException extends RuntimeException {
    public IdNotExistException(String message) {
        super(message);
    }
}
