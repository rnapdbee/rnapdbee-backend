package pl.poznan.put.rnapdbee.backend.shared.domain.param;


/**
 * Enum for NonCanonicalHandling.
 */
public enum NonCanonicalHandling {
    VISUALIZATION_ONLY("NC_visualization_only"),
    TEXT_AND_VISUALIZATION("NC_text_and_visualization"),
    IGNORE("NC_not_include");

    private final String archiveName;

    NonCanonicalHandling(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getArchiveName() {
        return archiveName;
    }
}
