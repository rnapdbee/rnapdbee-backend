package pl.poznan.put.rnapdbee.backend.shared.domain.param;


/**
 * Enum for Model Selection.
 */
public enum ModelSelection {
    FIRST("First_model_only"),
    ALL("All_models");

    private final String archiveName;

    ModelSelection(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getArchiveName() {
        return archiveName;
    }
}
