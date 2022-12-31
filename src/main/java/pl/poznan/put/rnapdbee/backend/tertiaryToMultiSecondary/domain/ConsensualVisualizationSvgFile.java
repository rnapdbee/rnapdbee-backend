package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

/**
 * Class representing structure of Consensual Visualization output from engine analyze.
 */
public class ConsensualVisualizationSvgFile extends ConsensualVisualization {
    private byte[] svgFile;

    private ConsensualVisualizationSvgFile() {
        super();
    }

    private ConsensualVisualizationSvgFile(byte[] svgFile) {
        this.svgFile = svgFile;
    }

    public byte[] getSvgFile() {
        return svgFile;
    }

    public static class Builder extends ConsensualVisualization.Builder<Builder> {
        private byte[] svgFile;

        @Override
        public ConsensualVisualizationSvgFile build() {
            return new ConsensualVisualizationSvgFile(this.getSvgFile());
        }

        @Override
        protected ConsensualVisualizationSvgFile.Builder self() {
            return this;
        }

        public byte[] getSvgFile() {
            return svgFile;
        }
    }
}
