package pl.poznan.put.rnapdbee.backend.downloadResult.exception;

public class BadEntriesSelectionListSizeException extends RuntimeException {
    public BadEntriesSelectionListSizeException(
            String messageFormat,
            int resultNumber,
            int expected,
            int occurred
    ) {
        super(String.format(messageFormat, resultNumber, expected, occurred));
    }
}
