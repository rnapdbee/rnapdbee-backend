package pl.poznan.put.rnapdbee.backend.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.poznan.put.rnapdbee.backend.analyzedFile.domain.AnalyzedFileEntityNotFoundException;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.FilenameNotSetException;
import pl.poznan.put.rnapdbee.backend.shared.exception.domain.IdNotFoundException;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            IdNotFoundException.class,
            FilenameNotSetException.class,
            AnalyzedFileEntityNotFoundException.class}
    )
    public ResponseEntity<ExceptionPattern> handleBadRequestException(
            RuntimeException exception) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ExceptionPattern exceptionPattern = new ExceptionPattern(
                exception.getMessage(),
                badRequest);

        return new ResponseEntity<>(exceptionPattern, badRequest);
    }
}
