package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class TertiaryToMultiSecondaryParams {
    private final ModelSelection modelSelection;
    private final boolean includeNonCanonical;
    private final boolean removeIsolated;
    private final VisualizationTool visualizationTool;

    private TertiaryToMultiSecondaryParams(
            ModelSelection modelSelection,
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool
    ) {
        this.modelSelection = modelSelection;
        this.includeNonCanonical = includeNonCanonical;
        this.removeIsolated = removeIsolated;
        this.visualizationTool = visualizationTool;
    }

    public static TertiaryToMultiSecondaryParams of(
            ModelSelection modelSelection,
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool
    ) {
        return new TertiaryToMultiSecondaryParams.Builder()
                .withModelSelection(modelSelection)
                .withIncludeNonCanonical(includeNonCanonical)
                .withRemoveIsolated(removeIsolated)
                .withVisualizationTool(visualizationTool)
                .build();
    }

    public ModelSelection getModelSelection() {
        return modelSelection;
    }

    public boolean isIncludeNonCanonical() {
        return includeNonCanonical;
    }

    public boolean isRemoveIsolated() {
        return removeIsolated;
    }

    public VisualizationTool getVisualizationTool() {
        return visualizationTool;
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

        public TertiaryToMultiSecondaryParams build() {
            return new TertiaryToMultiSecondaryParams(
                    this.getModelSelection(),
                    this.isIncludeNonCanonical(),
                    this.isRemoveIsolated(),
                    this.getVisualizationTool());
        }

        public ModelSelection getModelSelection() {
            return modelSelection;
        }

        public boolean isIncludeNonCanonical() {
            return includeNonCanonical;
        }

        public boolean isRemoveIsolated() {
            return removeIsolated;
        }

        public VisualizationTool getVisualizationTool() {
            return visualizationTool;
        }
    }
}
