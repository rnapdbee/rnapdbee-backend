package pl.poznan.put.rnapdbee.backend.downloadResult.exception;

public class BadModelsSelectionListSizeException extends RuntimeException {
    public BadModelsSelectionListSizeException(
            String messageFormat,
            int resultNumber,
            int expected,
            int occurred
    ) {
        super(String.format(messageFormat, resultNumber, expected, occurred));
    }
}
