package pl.poznan.put.rnapdbee.backend.images;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Component managing image resources.
 */
@Component
public class ImageComponent {
    private final Logger logger = LoggerFactory.getLogger(ImageComponent.class);
    private final String imageControllerPath = "/image";

    @Value("${svg.images.directory.path}")
    private String imagesPath;

    public String generateSvgUrl(final byte[] image) {
        final File imageFile = exportImage(image);
        return String.format("%s/%s", imageControllerPath, imageFile.getName());
    }

    private File exportImage(final byte[] image) {
        final File directory = new File(imagesPath);

        try {
            FileUtils.forceMkdir(directory);
            final File imageFile = File.createTempFile("RNApdbee", ".svg", directory);
            FileUtils.writeByteArrayToFile(imageFile, image);
            return imageFile;
        } catch (IOException e) {
            logger.error("Error occurred during exporting svg image.", e);
            throw new RuntimeException(e);
        }
    }

    public void deleteSvgImage(String pathToController) {
        String imageName = pathToController.substring(imageControllerPath.length() + 1);
        File imageFile = new File(String.format("%s/%s", imagesPath, imageName));
        if (!imageFile.delete())
            logger.warn(String.format("Failed to remove %s file", imageFile.getAbsolutePath()));
    }
}
