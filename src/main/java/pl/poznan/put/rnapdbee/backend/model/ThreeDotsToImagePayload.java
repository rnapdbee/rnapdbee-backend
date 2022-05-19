package pl.poznan.put.rnapdbee.backend.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload of (...) -> Image calculation
 */
public class ThreeDotsToImagePayload {

    @JsonProperty("fileContent")
    private String fileContent;

    @JsonProperty("structuralElementsHandling")
    private StructuralElementsHandling structuralElementsHandling;

    @JsonProperty("visualizationTool")
    private VisualizationTools visualizationTool;

    public String getFileContent() {
        return fileContent;
    }

    public StructuralElementsHandling getStructuralElementsHandling() {
        return structuralElementsHandling;
    }

    public VisualizationTools getVisualizationTool() {
        return visualizationTool;
    }

}

