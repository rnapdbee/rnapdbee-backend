package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class TertiaryToMultiSecondaryParams {
    private final boolean includeNonCanonical;
    private final boolean removeIsolated;
    private final VisualizationTool visualizationTool;

    private TertiaryToMultiSecondaryParams(
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool
    ) {
        this.includeNonCanonical = includeNonCanonical;
        this.removeIsolated = removeIsolated;
        this.visualizationTool = visualizationTool;
    }

    public static TertiaryToMultiSecondaryParams of(
            boolean includeNonCanonical,
            boolean removeIsolated,
            VisualizationTool visualizationTool
    ) {
        return new TertiaryToMultiSecondaryParams.Builder()
                .withIncludeNonCanonical(includeNonCanonical)
                .withRemoveIsolated(removeIsolated)
                .withVisualizationTool(visualizationTool)
                .build();
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
        private boolean includeNonCanonical;
        private boolean removeIsolated;
        private VisualizationTool visualizationTool;

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
                    this.isIncludeNonCanonical(),
                    this.isRemoveIsolated(),
                    this.getVisualizationTool());
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
