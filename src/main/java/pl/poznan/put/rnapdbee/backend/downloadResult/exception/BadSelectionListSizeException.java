package pl.poznan.put.rnapdbee.backend.downloadResult.exception;

public class BadSelectionListSizeException extends RuntimeException {

    public BadSelectionListSizeException(
            String messageFormat,
            int expected,
            int occurred
    ) {
        super(String.format(messageFormat, expected, occurred));
    }
}
