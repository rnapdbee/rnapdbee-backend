package pl.poznan.put.rnapdbee.backend.tertiaryToMultiSecondary.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConsensualVisualizationSvgFile extends ConsensualVisualization {
    @JsonProperty("svgFile")
    private final byte[] svgFile;

    private ConsensualVisualizationSvgFile(byte[] svgFile) {
        super();
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
