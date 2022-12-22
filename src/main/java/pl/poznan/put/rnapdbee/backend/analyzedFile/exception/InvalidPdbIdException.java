package pl.poznan.put.rnapdbee.backend.analyzedFile.exception;

public class InvalidPdbIdException extends RuntimeException {

    public InvalidPdbIdException(
            String messageFormat,
            String pdbId
    ) {
        super(String.format(messageFormat, pdbId));
    }
}
