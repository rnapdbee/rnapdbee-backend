package pl.poznan.put.rnapdbee.backend.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {
            IdNotExistsException.class,
            FileNameIsNullException.class,
            AnalyzedFileEntityNotExistException.class}
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
