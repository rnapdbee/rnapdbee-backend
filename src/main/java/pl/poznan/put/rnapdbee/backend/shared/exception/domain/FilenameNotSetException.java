package pl.poznan.put.rnapdbee.backend.shared.exception.domain;

public class FilenameNotSetException extends RuntimeException {
    public static final String CONTENT_DISPOSITION_NOT_SET =
            "Content-disposition header not set.";
    public static final String FILENAME_NOT_PARSABLE =
            "Filename non-parsable.";
    private static final String FILENAME_NOT_SET =
            "Filename not set.";

    public FilenameNotSetException() {
        super(FILENAME_NOT_SET);
    }

    public FilenameNotSetException(String message) {
        super(message);
    }
}
