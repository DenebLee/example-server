package kr.nanoit.exception;

public class UpdateFailedException extends RuntimeException {
    private final String reason;

    public UpdateFailedException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
