package pl.poznan.put.rnapdbee.backend.analyzedFile.exception;

public class InvalidPdbIdException extends RuntimeException {
    private static final String INVALID_PDB_ID_FORMAT = "Invalid PDB id: '%s', 4 characters required in id";

    public InvalidPdbIdException(String pdbId) {
        super(String.format(INVALID_PDB_ID_FORMAT, pdbId));
    }
}
