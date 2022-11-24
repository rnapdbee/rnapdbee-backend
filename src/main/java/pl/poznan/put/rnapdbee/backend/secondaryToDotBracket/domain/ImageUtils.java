package pl.poznan.put.rnapdbee.backend.secondaryToDotBracket.domain;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

public final class ImageUtils {

    public static Pair<File, String> generateSvgUrl(
            final ServletContext context,
            final byte[] image) {
        final File imageFile = ImageUtils.exportImage(context, image);
        return Pair.of(imageFile, String.format("%s/resources/tmp/%s", context.getContextPath(), imageFile.getName()));
    }

    private static File exportImage(
            final ServletContext context,
            final byte[] image) {
        final File directory = new File(context.getRealPath("resources/tmp"));

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
