package pl.poznan.put.rnapdbee.backend.shared.domain.output2D;

import pl.poznan.put.rnapdbee.backend.shared.domain.param.VisualizationTool;

/**
 * Class representing structure of image information output from engine analyze.
 */
public class ImageInformationByteArray extends ImageInformation {
    private final byte[] svgFile;

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
}
