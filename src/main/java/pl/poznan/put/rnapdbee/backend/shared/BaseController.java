package pl.poznan.put.rnapdbee.backend.shared;

import org.springframework.http.ContentDisposition;
import pl.poznan.put.rnapdbee.backend.shared.exception.FilenameNotSetException;

public abstract class BaseController {

    protected String validateContentDisposition(String contentDisposition) {
        if (contentDisposition == null || contentDisposition.isEmpty()) {
            throw new FilenameNotSetException(FilenameNotSetException.CONTENT_DISPOSITION_NOT_SET);
        }

        try {
            String filename = ContentDisposition.parse(contentDisposition).getFilename();
            if (filename == null || filename.isEmpty())
                throw new FilenameNotSetException();
            return filename;

        } catch (IllegalArgumentException exception) {
            throw new FilenameNotSetException(FilenameNotSetException.FILENAME_NOT_PARSABLE);
        }
    }
}
