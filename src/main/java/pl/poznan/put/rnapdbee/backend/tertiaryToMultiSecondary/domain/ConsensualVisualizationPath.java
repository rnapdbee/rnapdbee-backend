package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

/**
 * Class representing structure of Consensual Visualization response and entity.
 */
public class ConsensualVisualizationPath extends ConsensualVisualization {
    private final String pathToSVGImage;

    private ConsensualVisualizationPath(String pathToSVGImage) {
        this.pathToSVGImage = pathToSVGImage;
    }

    public static ConsensualVisualizationPath of(
            String pathToSVGImage
    ) {
        return new ConsensualVisualizationPath.Builder()
                .withPathToSVGImage(pathToSVGImage)
                .build();
    }

    public String getPathToSVGImage() {
        return pathToSVGImage;
    }

    public static class Builder extends ConsensualVisualization.Builder<Builder> {
        private String pathToSVGImage;

        public Builder withPathToSVGImage(String pathToSVGImage) {
            this.pathToSVGImage = pathToSVGImage;
            return self();
        }

        @Override
        public ConsensualVisualizationPath build() {
            return new ConsensualVisualizationPath(this.getPathToSVGImage());
        }

        @Override
        protected ConsensualVisualizationPath.Builder self() {
            return this;
        }

        public String getPathToSVGImage() {
            return pathToSVGImage;
        }
    }
}
