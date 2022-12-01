package pl.poznan.put.rnapdbee.backend.shared.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;


public class ImageInformationByteArray extends ImageInformationOutput {
    private byte[] svgFile;

    private ImageInformationByteArray(
            byte[] svgFile,
            VisualizationTool successfulVisualizationTool,
            VisualizationTool failedVisualizationTool,
            String drawingResult) {
        super(successfulVisualizationTool, failedVisualizationTool, drawingResult);
        this.svgFile = svgFile;
    }

    public byte[] getSvgFile() {
        return svgFile;
    }

    public static class Builder extends ImageInformationOutput.Builder<Builder> {

        private byte[] svgFile;

        @Override
        public ImageInformationByteArray build() {
            return new ImageInformationByteArray(
                    this.getSvgFile(),
                    this.getSuccessfulVisualizationTool(),
                    this.getFailedVisualizationTool(),
                    this.getDrawingResult());
        }

        @Override
        protected ImageInformationByteArray.Builder self() {
            return this;
        }

        public byte[] getSvgFile() {
            return svgFile;
        }
    }
}
