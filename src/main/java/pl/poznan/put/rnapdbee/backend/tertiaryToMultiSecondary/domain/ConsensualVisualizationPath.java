package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

public class ConsensualVisualizationPath extends ConsensualVisualization {
    private final String pathToSvgImage;

    private ConsensualVisualizationPath(String pathToSvgImage) {
        this.pathToSvgImage = pathToSvgImage;
    }

    public static ConsensualVisualizationPath of(
            String pathToSvgImage
    ) {
        return new ConsensualVisualizationPath.Builder()
                .withPathToSvgImage(pathToSvgImage)
                .build();
    }

    public String getPathToSvgImage() {
        return pathToSvgImage;
    }

    public static class Builder extends ConsensualVisualization.Builder<Builder> {
        private String pathToSvgImage;

        public Builder withPathToSvgImage(String pathToSvgImage) {
            this.pathToSvgImage = pathToSvgImage;
            return self();
        }

        @Override
        public ConsensualVisualizationPath build() {
            return new ConsensualVisualizationPath(this.getPathToSvgImage());
        }

        @Override
        protected ConsensualVisualizationPath.Builder self() {
            return this;
        }

        public String getPathToSvgImage() {
            return pathToSvgImage;
        }
    }
}
