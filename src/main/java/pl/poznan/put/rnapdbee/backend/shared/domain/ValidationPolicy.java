package pl.poznan.put.rnapdbee.backend.shared.domain;

import org.springframework.http.ContentDisposition;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.FilenameIsNullException;

public abstract class ValidationPolicy {
    public static String validateFilename(String contentDisposition) {
        String filename = ContentDisposition.parse(contentDisposition).getFilename();
        if (filename == null)
            throw new FilenameIsNullException("filename in 'Content-Disposition' header must not be null");

        return filename;
    }
}
