package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

/**
 * Class representing tertiary to dot bracket analysis params
 */
public class TertiaryToDotBracketParams {
    private final ModelSelection modelSelection;
    private final AnalysisTool analysisTool;
    private final NonCanonicalHandling nonCanonicalHandling;
    private final boolean removeIsolated;
    private final StructuralElementsHandling structuralElementsHandling;
    private final VisualizationTool visualizationTool;

    private TertiaryToDotBracketParams(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool
    ) {
        this.modelSelection = modelSelection;
        this.analysisTool = analysisTool;
        this.nonCanonicalHandling = nonCanonicalHandling;
        this.removeIsolated = removeIsolated;
        this.structuralElementsHandling = structuralElementsHandling;
        this.visualizationTool = visualizationTool;
    }

    public static TertiaryToDotBracketParams of(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool
    ) {
        return new TertiaryToDotBracketParams.Builder()
                .withModelSelection(modelSelection)
                .withAnalysisTool(analysisTool)
                .withNonCanonicalHandling(nonCanonicalHandling)
                .withRemoveIsolated(removeIsolated)
                .withStructuralElementsHandling(structuralElementsHandling)
                .withVisualizationTool(visualizationTool)
                .build();
    }

    public ModelSelection getModelSelection() {
        return modelSelection;
    }

    public AnalysisTool getAnalysisTool() {
        return analysisTool;
    }

    public NonCanonicalHandling getNonCanonicalHandling() {
        return nonCanonicalHandling;
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

        public TertiaryToDotBracketParams build() {
            return new TertiaryToDotBracketParams(
                    this.getModelSelection(),
                    this.getAnalysisTool(),
                    this.getNonCanonicalHandling(),
                    this.isRemoveIsolated(),
                    this.getStructuralElementsHandling(),
                    this.getVisualizationTool());
        }

        public ModelSelection getModelSelection() {
            return modelSelection;
        }

        public AnalysisTool getAnalysisTool() {
            return analysisTool;
        }

        public NonCanonicalHandling getNonCanonicalHandling() {
            return nonCanonicalHandling;
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
