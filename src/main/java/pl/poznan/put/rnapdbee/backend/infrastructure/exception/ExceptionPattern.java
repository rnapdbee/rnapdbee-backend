package pl.poznan.put.rnapdbee.backend.infrastructure.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * Class representing api exception pattern
 */
public class ExceptionPattern {

    private String message;
    private Integer status;
    private String error;
    private ZonedDateTime timestamp;

    /**
     * ApiException class constructor
     *
     * @param message    Exception message
     * @param httpStatus Exception status code
     */
    public ExceptionPattern(
            String message,
            HttpStatus httpStatus
    ) {
        this.message = message;
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.timestamp = ZonedDateTime.now();
    }

    private ExceptionPattern() {
    }

    public ExceptionPattern(
            String message,
            Integer status,
            String error
    ) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.timestamp = ZonedDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("{message='%s', status='%s', error='%s', timestamp='%s'}",
                message,
                status,
                error,
                timestamp);
    }
}
