package pl.poznan.put.rnapdbee.backend.shared.exception;

public class EngineReturnedException extends RuntimeException {

    private final Integer status;
    private final String error;

    public EngineReturnedException(
            String message,
            Integer status,
            String error
    ) {
        super(message);
        this.status = status;
        this.error = error;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
