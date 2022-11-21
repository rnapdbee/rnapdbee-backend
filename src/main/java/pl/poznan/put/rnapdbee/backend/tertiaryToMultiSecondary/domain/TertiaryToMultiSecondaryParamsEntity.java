package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class TertiaryToMultiSecondaryParamsEntity {
    private final ModelSelection modelSelection;
    private final boolean includeNonCanonical;
    private final boolean removeIsolated;
    private final VisualizationTool visualizationTool;

    private TertiaryToMultiSecondaryParamsEntity(Builder builder) {
        this.modelSelection = builder.modelSelection;
        this.includeNonCanonical = builder.includeNonCanonical;
        this.removeIsolated = builder.removeIsolated;
        this.visualizationTool = builder.visualizationTool;
    }

    public static class Builder {
        private ModelSelection modelSelection;
        private boolean includeNonCanonical;
        private boolean removeIsolated;
        private VisualizationTool visualizationTool;

        public Builder withModelSelection(ModelSelection modelSelection) {
            this.modelSelection = modelSelection;
            return this;
        }

        public Builder withIncludeNonCanonical(boolean includeNonCanonical) {
            this.includeNonCanonical = includeNonCanonical;
            return this;
        }

        public Builder withRemoveIsolated(boolean removeIsolated) {
            this.removeIsolated = removeIsolated;
            return this;
        }

        public Builder withVisualizationTool(VisualizationTool visualizationTool) {
            this.visualizationTool = visualizationTool;
            return this;
        }

        public TertiaryToMultiSecondaryParamsEntity build() {
            return new TertiaryToMultiSecondaryParamsEntity(this);
        }
    }
}
