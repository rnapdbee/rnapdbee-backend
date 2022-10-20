package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.StructuralElementsHandling;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class SecondaryToDotBracketParamsEntity {
    private boolean removeIsolated;
    private StructuralElementsHandling structuralElementsHandling;
    private VisualizationTool visualizationTool;

    public SecondaryToDotBracketParamsEntity(
            boolean removeIsolated,
            StructuralElementsHandling structuralElementsHandling,
            VisualizationTool visualizationTool) {
        this.removeIsolated = removeIsolated;
        this.structuralElementsHandling = structuralElementsHandling;
        this.visualizationTool = visualizationTool;
    }
}
