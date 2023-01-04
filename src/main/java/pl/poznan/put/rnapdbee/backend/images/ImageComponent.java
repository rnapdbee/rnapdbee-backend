package pl.poznan.put.rnapdbee.backend.images;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Component managing creation of image resources
 */
@Component
public class ImageComponent {

    @Value("${spring.svg.images.directory.path}")
    private String imagesPath;

    public String generateSvgUrl(final byte[] image) {
        final File imageFile = exportImage(image);
        String imageControllerPath = "/image";
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
            throw new RuntimeException(e);
        }
    }
}
