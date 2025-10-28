package pl.poznan.put.rnapdbee.backend.shared.domain.param;

/**
 * Enum for base pair analyzers supported by rnapdbee 3.0.
 */
public enum AnalysisTool {
    RNAPOLIS("RNApolis_Annotator"),
    FR3D_PYTHON("FR3D"),
    BPNET("bpnet"),
    BARNABA("baRNAba"),
    RNAVIEW("RNAView"),
    MC_ANNOTATE("MC_Annotate"),
    MAXIT("maxit");

    private final String archiveName;

    AnalysisTool(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getArchiveName() {
        return archiveName;
    }
}
