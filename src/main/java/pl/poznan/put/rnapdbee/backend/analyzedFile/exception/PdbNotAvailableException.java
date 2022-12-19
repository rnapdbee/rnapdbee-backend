package pl.poznan.put.rnapdbee.backend.analyzedFile.exception;


public class PdbNotAvailableException extends RuntimeException {
    private static final String PDB_NOT_AVAILABLE_FORMAT = "Protein Data Bank not available";

    public PdbNotAvailableException() {
        super(PDB_NOT_AVAILABLE_FORMAT);
    }
}
