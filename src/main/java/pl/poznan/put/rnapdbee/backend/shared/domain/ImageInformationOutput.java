package pl.poznan.put.rnapdbee.backend.shared.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain.EngineImageInformationOutputResponse;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;


public class ImageInformationOutput {
    private String pathToSVGImage;
    private VisualizationTool successfulVisualizationTool;
    private VisualizationTool failedVisualizationTool;
    private String drawingResult;

    private ImageInformationOutput(
            String pathToSVGImage,
            VisualizationTool successfulVisualizationTool,
            VisualizationTool failedVisualizationTool,
            String drawingResult) {
        this.pathToSVGImage = pathToSVGImage;
        this.successfulVisualizationTool = successfulVisualizationTool;
        this.failedVisualizationTool = failedVisualizationTool;
        this.drawingResult = drawingResult;
    }

    public String getPathToSVGImage() {
        return pathToSVGImage;
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

    public static class Builder {
        private String pathToSVGImage;
        private VisualizationTool successfulVisualizationTool;
        private VisualizationTool failedVisualizationTool;
        private String drawingResult;

        public Builder withPathToSVGImage(String pathToSVGImage) {
            this.pathToSVGImage = pathToSVGImage;
            return this;
        }

        public Builder withEngineOutput2DResponse(EngineImageInformationOutputResponse engineImageInformationOutputResponse) {
            this.successfulVisualizationTool = engineImageInformationOutputResponse.getSuccessfulVisualizationTool();
            this.failedVisualizationTool = engineImageInformationOutputResponse.getFailedVisualizationTool();
            this.drawingResult = engineImageInformationOutputResponse.getDrawingResult();
            return this;
        }

        public ImageInformationOutput build() {
            return new ImageInformationOutput(
                    this.getPathToSVGImage(),
                    this.getSuccessfulVisualizationTool(),
                    this.getFailedVisualizationTool(),
                    this.getDrawingResult());
        }

        public String getPathToSVGImage() {
            return pathToSVGImage;
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
}
