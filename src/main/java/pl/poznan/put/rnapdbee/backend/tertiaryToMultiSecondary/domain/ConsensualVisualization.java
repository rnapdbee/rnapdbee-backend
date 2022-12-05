package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;


/**
 * DTO class for Consensual Visualization
 */
public class ConsensualVisualization {
    private final byte[] svgFile;

    private ConsensualVisualization(byte[] svgFile) {
        this.svgFile = svgFile;
    }

    public byte[] getSvgFile() {
        return svgFile;
    }

    public static final class Builder {
        private byte[] svgFile;

        public Builder withSvgFile(byte[] svgFile) {
            this.svgFile = svgFile;
            return this;
        }

        public ConsensualVisualization build() {
            return new ConsensualVisualization(this.getSvgFile());
        }

        public byte[] getSvgFile() {
            return svgFile;
        }
    }
}
