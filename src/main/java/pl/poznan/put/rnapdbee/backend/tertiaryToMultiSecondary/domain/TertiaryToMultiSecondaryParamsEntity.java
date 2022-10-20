package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class TertiaryToMultiSecondaryParamsEntity {
    private ModelSelection modelSelection;
    private boolean includeNonCanonical;
    private boolean removeIsolated;
    private VisualizationTool visualizationTool;

    public TertiaryToMultiSecondaryParamsEntity(
            ModelSelection modelSelection,
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool) {
        this.modelSelection = modelSelection;
        this.includeNonCanonical = includeNonCanonical;
        this.removeIsolated = removeIsolated;
        this.visualizationTool = visualizationTool;
    }
}
