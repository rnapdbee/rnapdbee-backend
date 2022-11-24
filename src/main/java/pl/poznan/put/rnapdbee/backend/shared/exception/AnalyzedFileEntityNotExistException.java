package pl.poznan.put.rnapdbee.backend.shared.exception;

public class AnalyzedFileEntityNotExistException extends RuntimeException {
    public AnalyzedFileEntityNotExistException(String message) {
        super(message);
    }
}
