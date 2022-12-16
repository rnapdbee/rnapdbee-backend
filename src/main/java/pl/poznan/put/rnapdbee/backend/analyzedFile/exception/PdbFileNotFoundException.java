package pl.poznan.put.rnapdbee.backend.analyzedFile.exception;

public class PdbFileNotFoundException extends RuntimeException {
    private static final String PDB_FILE_NOT_FOUND_FORMAT = "File '%s.cif' not found";

    public PdbFileNotFoundException(String pdbId) {
        super(String.format(PDB_FILE_NOT_FOUND_FORMAT, pdbId));
    }
}
