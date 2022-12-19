package pl.poznan.put.rnapdbee.backend.analyzedFile.exception;

public class PdbFileUnzipException extends RuntimeException {
    private static final String PDB_FILE_UNZIP_FORMAT = "Pdb file '%s.cif.gz' unzip problem";

    public PdbFileUnzipException(String pdbId) {
        super(String.format(PDB_FILE_UNZIP_FORMAT, pdbId));
    }
}
