package pl.poznan.put.rnapdbee.backend.infrastructure.exception;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.AnalyzedFileEntityNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.InvalidPdbIdException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileNotFoundException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbFileUnzipException;
import pl.poznan.put.rnapdbee.backend.analyzedFile.exception.PdbNotAvailableException;
import pl.poznan.put.rnapdbee.backend.shared.MessageProvider;
import pl.poznan.put.rnapdbee.backend.shared.exception.DocumentExpiredException;
import pl.poznan.put.rnapdbee.backend.shared.exception.EngineNotAvailableException;
import pl.poznan.put.rnapdbee.backend.shared.exception.EngineReturnedException;
import pl.poznan.put.rnapdbee.backend.shared.exception.FilenameNotSetException;
import pl.poznan.put.rnapdbee.backend.shared.exception.IdNotFoundException;

@ControllerAdvice
public class ApiExceptionHandler {

    private final MessageProvider messageProvider;
    private final Logger logger;

    public ApiExceptionHandler(
            MessageProvider messageProvider,
            Logger logger
    ) {
        this.messageProvider = messageProvider;
        this.logger = logger;
    }

    @ExceptionHandler(value = {
            FilenameNotSetException.class,
            InvalidPdbIdException.class,
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
        HttpStatus httpStatus = HttpStatus.resolve(exception.getStatus());

        ExceptionPattern exceptionPattern = new ExceptionPattern(
                exception.getMessage(),
                exception.getStatus(),
                exception.getError());

        return new ResponseEntity<>(exceptionPattern, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionPattern> handleAllUncaughtException(Exception exception) {
        logger.error(String.format("Unknown error occurred: %s", exception.getMessage()), exception);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return prepareResponseEntity(messageProvider.getMessage("api.exception.unknown.error"), httpStatus);
    }

    private ResponseEntity<ExceptionPattern> prepareResponseEntity(
            String message,
            HttpStatus httpStatus
    ) {
        ExceptionPattern exceptionPattern = new ExceptionPattern(
                message,
                httpStatus);

        return new ResponseEntity<>(exceptionPattern, httpStatus);
    }

}
