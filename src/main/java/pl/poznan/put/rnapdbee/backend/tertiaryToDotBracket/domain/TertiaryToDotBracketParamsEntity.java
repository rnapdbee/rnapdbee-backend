package pl.poznan.put.rnapdbee.backend.tertiaryToDotBracket.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.AnalysisTool;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.ModelSelection;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.NonCanonicalHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class TertiaryToDotBracketParamsEntity {
    private ModelSelection modelSelection;
    private AnalysisTool analysisTool;
    private NonCanonicalHandling nonCanonicalHandling;
    private boolean removeIsolated;
    private StructuralElementsHandling structuralElementsHandling;
    private VisualizationTool visualizationTool;

    public TertiaryToDotBracketParamsEntity(
            ModelSelection modelSelection,
            AnalysisTool analysisTool,
            NonCanonicalHandling nonCanonicalHandling,
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool) {
        this.modelSelection = modelSelection;
        this.analysisTool = analysisTool;
        this.nonCanonicalHandling = nonCanonicalHandling;
        this.removeIsolated = removeIsolated;
        this.structuralElementsHandling = structuralElementsHandling;
        this.visualizationTool = visualizationTool;
    }
}
