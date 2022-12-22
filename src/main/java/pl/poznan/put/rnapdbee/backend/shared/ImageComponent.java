package pl.poznan.put.rnapdbee.backend.shared;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

/**
 * Component managing creation of image resources.
 */
@Component
public class ImageComponent {
    private final ServletContext servletContext;

    @Autowired
    private ImageComponent(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String generateSvgUrl(final byte[] image) {
        final File imageFile = exportImage(image);
        return String.format("%s/resources/tmp/%s", servletContext.getContextPath(), imageFile.getName());
    }

    private File exportImage(final byte[] image) {
        final File directory = new File(servletContext.getRealPath("resources/tmp"));

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
