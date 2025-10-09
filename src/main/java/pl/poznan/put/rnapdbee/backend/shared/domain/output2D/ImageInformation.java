package pl.poznan.put.rnapdbee.backend.shared.domain.output2D;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

/**
 * Class representing structure of image information.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, defaultImpl = ImageInformationByteArray.class)
public abstract class ImageInformation {
    protected final VisualizationTool successfulVisualizationTool;
    protected final VisualizationTool failedVisualizationTool;
    protected final DrawingResult drawingResult;

    protected ImageInformation(
            VisualizationTool successfulVisualizationTool,
            VisualizationTool failedVisualizationTool,
            DrawingResult drawingResult) {
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

    public DrawingResult getDrawingResult() {
        return drawingResult;
    }

    public boolean wasDrawn() {
        return drawingResult != DrawingResult.FAILED_BY_BOTH_DRAWERS && drawingResult != DrawingResult.NOT_DRAWN;
    }

    protected abstract static class Builder<B extends Builder<B>> {
        private VisualizationTool successfulVisualizationTool;
        private VisualizationTool failedVisualizationTool;
        private DrawingResult drawingResult;

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

        public DrawingResult getDrawingResult() {
            return drawingResult;
        }
    }
}
