package pl.poznan.put.rnapdbee.backend.shared.domain.output2D;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

/**
 * DTO class representing structure of image information.
 */
public abstract class ImageInformation {
    protected final VisualizationTool successfulVisualizationTool;
    protected final VisualizationTool failedVisualizationTool;
    protected final String drawingResult;

    protected ImageInformation(
            VisualizationTool successfulVisualizationTool,
            VisualizationTool failedVisualizationTool,
            String drawingResult) {
        this.successfulVisualizationTool = successfulVisualizationTool;
        this.failedVisualizationTool = failedVisualizationTool;
        this.drawingResult = drawingResult;
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

    protected abstract static class Builder<B extends Builder<B>> {
        private VisualizationTool successfulVisualizationTool;
        private VisualizationTool failedVisualizationTool;
        private String drawingResult;

        protected abstract B self();

        public B withEngineOutput2DResponse(ImageInformationByteArray imageInformationByteArray) {
            this.successfulVisualizationTool = imageInformationByteArray.getSuccessfulVisualizationTool();
            this.failedVisualizationTool = imageInformationByteArray.getFailedVisualizationTool();
            this.drawingResult = imageInformationByteArray.getDrawingResult();
            return self();
        }

        protected abstract ImageInformation build();

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
