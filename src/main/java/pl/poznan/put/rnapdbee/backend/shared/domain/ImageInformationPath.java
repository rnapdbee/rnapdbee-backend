package pl.poznan.put.rnapdbee.backend.shared.domain;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

public class ImageInformationPath extends ImageInformationOutput {
    private final String pathToSVGImage;

    private ImageInformationPath(
            String pathToSVGImage,
            VisualizationTool successfulVisualizationTool,
            VisualizationTool failedVisualizationTool,
            String drawingResult) {
        super(successfulVisualizationTool, failedVisualizationTool, drawingResult);
        this.pathToSVGImage = pathToSVGImage;
    }

    public static ImageInformationPath of(
            ImageInformationByteArray imageInformationByteArray,
            String pathToSVGImage
    ) {
        return new ImageInformationPath.Builder()
                .withEngineOutput2DResponse(imageInformationByteArray)
                .withPathToSVGImage(pathToSVGImage)
                .build();
    }

    public String getPathToSVGImage() {
        return pathToSVGImage;
    }

    public static class Builder extends ImageInformationOutput.Builder<Builder> {

        private String pathToSVGImage;

        @Override
        public ImageInformationPath build() {
            return new ImageInformationPath(
                    this.getPathToSVGImage(),
                    this.getSuccessfulVisualizationTool(),
                    this.getFailedVisualizationTool(),
                    this.getDrawingResult());
        }

        @Override
        protected ImageInformationPath.Builder self() {
            return this;
        }

        public Builder withPathToSVGImage(String pathToSVGImage) {
            this.pathToSVGImage = pathToSVGImage;
            return this;
        }

        public String getPathToSVGImage() {
            return pathToSVGImage;
        }
    }
}
