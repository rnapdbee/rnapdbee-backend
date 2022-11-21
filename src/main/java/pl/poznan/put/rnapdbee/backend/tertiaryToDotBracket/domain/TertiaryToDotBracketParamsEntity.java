package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class TertiaryToDotBracketParamsEntity {
    private final ModelSelection modelSelection;
    private final AnalysisTool analysisTool;
    private final NonCanonicalHandling nonCanonicalHandling;
    private final boolean removeIsolated;
    private final StructuralElementsHandling structuralElementsHandling;
    private final VisualizationTool visualizationTool;

    private TertiaryToDotBracketParamsEntity(Builder builder) {
        this.modelSelection = builder.modelSelection;
        this.analysisTool = builder.analysisTool;
        this.nonCanonicalHandling = builder.nonCanonicalHandling;
        this.removeIsolated = builder.removeIsolated;
        this.structuralElementsHandling = builder.structuralElementsHandling;
        this.visualizationTool = builder.visualizationTool;
    }

    public static class Builder {
        private ModelSelection modelSelection;
        private AnalysisTool analysisTool;
        private NonCanonicalHandling nonCanonicalHandling;
        private boolean removeIsolated;
        private StructuralElementsHandling structuralElementsHandling;
        private VisualizationTool visualizationTool;

        public Builder withModelSelection(ModelSelection modelSelection) {
            this.modelSelection = modelSelection;
            return this;
        }

        public Builder withAnalysisTool(AnalysisTool analysisTool) {
            this.analysisTool = analysisTool;
            return this;
        }

        public Builder withNonCanonicalHandling(NonCanonicalHandling nonCanonicalHandling) {
            this.nonCanonicalHandling = nonCanonicalHandling;
            return this;
        }

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

        public TertiaryToDotBracketParamsEntity build() {
            return new TertiaryToDotBracketParamsEntity(this);
        }
    }
}
