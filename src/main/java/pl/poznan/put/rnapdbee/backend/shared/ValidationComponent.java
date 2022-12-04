package pl.poznan.put.rnapdbee.backend.shared;

import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.FilenameNotSetException;

@Component
public class ValidationComponent {
    public String validateFilename(String contentDisposition) {
        if (contentDisposition == null) {
            throw new FilenameNotSetException(FilenameNotSetException.CONTENT_DISPOSITION_NOT_SET);
        }

        try {
            String filename = ContentDisposition.parse(contentDisposition).getFilename();
            if (filename == null)
                throw new FilenameNotSetException();
            return filename;

        } catch (IllegalArgumentException exception) {
            throw new FilenameNotSetException(FilenameNotSetException.FILENAME_NOT_PARSABLE);
        }
    }
}
