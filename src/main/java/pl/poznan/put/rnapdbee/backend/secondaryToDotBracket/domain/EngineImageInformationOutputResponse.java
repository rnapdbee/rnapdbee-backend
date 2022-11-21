package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class EngineImageInformationOutputResponse {
    @JsonProperty("svgFile")
    private byte[] svgFile;

    @JsonProperty("successfulVisualizationTool")
    private VisualizationTool successfulVisualizationTool;

    @JsonProperty("failedVisualizationTool")
    private VisualizationTool failedVisualizationTool;

    @JsonProperty("drawingResult")
    private String drawingResult;

    public byte[] getSvgFile() {
        return svgFile;
    }

    public VisualizationTool getSuccessfulVisualizationTool() {
        return successfulVisualizationTool;
    }

    public VisualizationTool getFailedVisualizationTool() {
        return failedVisualizationTool;
    }

    public String getDrawingResult() {
        return drawingResult;
    }
}
