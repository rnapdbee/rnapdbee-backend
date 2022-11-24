package pl.poznan.put.rnapdbee.backend.shared.exception;

import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ExceptionPattern {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;

    /**
     * ApiException class constructor
     *
     * @param message    Exception message
     * @param httpStatus Exception status code
     */
    public ExceptionPattern(
            String message,
            HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = ZonedDateTime.now(ZoneId.of("Z"));
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
