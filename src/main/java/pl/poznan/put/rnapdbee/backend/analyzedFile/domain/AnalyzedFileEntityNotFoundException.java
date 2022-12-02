package pl.poznan.put.rnapdbee.backend.analyzedFile.domain;

public class AnalyzedFileEntityNotFoundException extends RuntimeException {
    private static final String FILE_NOT_FOUND = "File to reanalyze not found";

    public AnalyzedFileEntityNotFoundException() {
        super(FILE_NOT_FOUND);
    }
}
