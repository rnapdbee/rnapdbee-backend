package pl.poznan.put.rnapdbee.backend.analyzedFile.exception;

public class PdbFileNotFoundException extends RuntimeException {

    public PdbFileNotFoundException(
            String messageFormat,
            String pdbId
    ) {
        super(String.format(messageFormat, pdbId));
    }
}
