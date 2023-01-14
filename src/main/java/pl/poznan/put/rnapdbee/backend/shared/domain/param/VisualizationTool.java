package pl.poznan.put.rnapdbee.backend.shared.domain.param;


/**
 * Enum for VisualizationTools.
 */
public enum VisualizationTool {
    RNA_PUZZLER("RNApuzzler"),
    VARNA("VARNA"),
    PSEUDO_VIEWER("PseudoViewer"),
    R_CHIE("R_Chie"),
    NONE("No_image");

    private final String archiveName;

    VisualizationTool(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getArchiveName() {
        return archiveName;
    }
}
