package pl.poznan.put.rnapdbee.backend.analyzedFile.exception;

public class PdbFileUnzipException extends RuntimeException {

    public PdbFileUnzipException(
            String messageFormat,
            String pdbId
    ) {
        super(String.format(messageFormat, pdbId));
    }
}
