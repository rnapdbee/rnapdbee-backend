package pl.poznan.put.rnapdbee.backend.shared.domain.param;


/**
 * Enum for StructuralElementsHandling.
 */
public enum StructuralElementsHandling {
    USE_PSEUDOKNOTS("Include_pseudoknots"),
    IGNORE_PSEUDOKNOTS("Ignore_pseudoknots");

    private final String archiveName;

    StructuralElementsHandling(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getArchiveName() {
        return archiveName;
    }
}
