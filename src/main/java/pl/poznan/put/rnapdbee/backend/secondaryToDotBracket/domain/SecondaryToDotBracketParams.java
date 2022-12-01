package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class SecondaryToDotBracketParams {
    private final boolean removeIsolated;
    private final StructuralElementsHandling structuralElementsHandling;
    private final VisualizationTool visualizationTool;

    private SecondaryToDotBracketParams(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool
    ) {
        this.removeIsolated = removeIsolated;
        this.structuralElementsHandling = structuralElementsHandling;
        this.visualizationTool = visualizationTool;
    }

    public static SecondaryToDotBracketParams of(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool
    ) {
        return new SecondaryToDotBracketParams.Builder()
                .withRemoveIsolated(removeIsolated)
                .withStructuralElementsHandling(structuralElementsHandling)
                .withVisualizationTool(visualizationTool)
                .build();
    }

    public boolean isRemoveIsolated() {
        return removeIsolated;
    }

    public StructuralElementsHandling getStructuralElementsHandling() {
        return structuralElementsHandling;
    }

    public VisualizationTool getVisualizationTool() {
        return visualizationTool;
    }

    public static class Builder {
        private boolean removeIsolated;
        private StructuralElementsHandling structuralElementsHandling;
        private VisualizationTool visualizationTool;

        public Builder withRemoveIsolated(boolean removeIsolated) {
            this.removeIsolated = removeIsolated;
            return this;
        }

        public Builder withStructuralElementsHandling(StructuralElementsHandling structuralElementsHandling) {
            this.structuralElementsHandling = structuralElementsHandling;
            return this;
        }

        public Builder withVisualizationTool(VisualizationTool visualizationTool) {
            this.visualizationTool = visualizationTool;
            return this;
        }

        public SecondaryToDotBracketParams build() {
            return new SecondaryToDotBracketParams(
                    this.isRemoveIsolated(),
                    this.getStructuralElementsHandling(),
                    this.getVisualizationTool());
        }

        public boolean isRemoveIsolated() {
            return removeIsolated;
        }

        public StructuralElementsHandling getStructuralElementsHandling() {
            return structuralElementsHandling;
        }

        public VisualizationTool getVisualizationTool() {
            return visualizationTool;
        }
    }
}
