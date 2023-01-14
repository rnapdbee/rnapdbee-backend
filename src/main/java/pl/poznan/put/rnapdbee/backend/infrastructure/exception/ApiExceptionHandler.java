package pl.poznan.put.rnapdbee.backend.infrastructure.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.AnalyzedFileEntityNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.InvalidPdbIdException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileUnzipException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbNotAvailableException;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadEntriesSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadModelsSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.downloadResult.exception.BadSelectionListSizeException;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.exception.DocumentExpiredException;
import pl.poznan.put.rnapdbee.backend.shared.exception.EngineNotAvailableException;
import pl.poznan.put.rnapdbee.backend.shared.exception.EngineReturnedException;
import pl.poznan.put.rnapdbee.backend.shared.exception.FilenameNotSetException;
import pl.poznan.put.rnapdbee.backend.shared.exception.IdNotFoundException;

import java.util.Objects;

/**
 * Class responsible for handling api exceptions
 */
@ControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private final MessageProvider messageProvider;

    public ApiExceptionHandler(
            MessageProvider messageProvider
    ) {
        this.messageProvider = messageProvider;
    }

    @ExceptionHandler(value = {
            FilenameNotSetException.class,
            InvalidPdbIdException.class,
            BadSelectionListSizeException.class,
            BadEntriesSelectionListSizeException.class,
            BadModelsSelectionListSizeException.class
    })
    public ResponseEntity<ExceptionPattern> handleBadRequestException(RuntimeException exception) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        return prepareResponseEntity(exception.getMessage(), httpStatus);
    }

    @ExceptionHandler(value = {
            IdNotFoundException.class,
            AnalyzedFileEntityNotFoundException.class,
            PdbFileNotFoundException.class,
            DocumentExpiredException.class,
    })
    public ResponseEntity<ExceptionPattern> handleNotFoundException(RuntimeException exception) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        return prepareResponseEntity(exception.getMessage(), httpStatus);
    }

    @ExceptionHandler(PdbFileUnzipException.class)
    public ResponseEntity<ExceptionPattern> handleInternalServerErrorException(RuntimeException exception) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return prepareResponseEntity(exception.getMessage(), httpStatus);
    }

    @ExceptionHandler(value = {
            PdbNotAvailableException.class,
            EngineNotAvailableException.class,
    })
    public ResponseEntity<ExceptionPattern> handleServiceUnavailableException(RuntimeException exception) {
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

        return prepareResponseEntity(exception.getMessage(), httpStatus);
    }

    @ExceptionHandler(EngineReturnedException.class)
    public ResponseEntity<ExceptionPattern> handleEngineReturnedException(EngineReturnedException exception) {
        HttpStatus httpStatus = Objects.requireNonNullElse(
                HttpStatus.resolve(exception.getStatus()),
                HttpStatus.INTERNAL_SERVER_ERROR);

        ExceptionPattern exceptionPattern = new ExceptionPattern(
                Objects.requireNonNullElse(
                        exception.getMessage(),
                        messageProvider.getMessage(MessageProvider.Message.UNEXPECTED_ANALYSIS_ERROR)),
                Objects.requireNonNullElse(
                        exception.getStatus(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()),
                Objects.requireNonNullElse(
                        exception.getError(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));

        logger.error(String.format("Exception response: %s", exceptionPattern));
        return new ResponseEntity<>(exceptionPattern, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionPattern> handleAllUncaughtException(Exception exception) {
        logger.error(String.format("Unexpected error occurred: %s", exception.getMessage()), exception);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return prepareResponseEntity(messageProvider.getMessage(MessageProvider.Message.UNEXPECTED_ERROR), httpStatus);
    }

    private ResponseEntity<ExceptionPattern> prepareResponseEntity(
            String message,
            HttpStatus httpStatus
    ) {
        ExceptionPattern exceptionPattern = new ExceptionPattern(
                message,
                httpStatus);

        logger.error(String.format("Exception response: %s", exceptionPattern));
        return new ResponseEntity<>(exceptionPattern, httpStatus);
    }

}
