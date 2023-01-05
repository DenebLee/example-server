package kr.nanoit.exception;

public class DeleteFailedException extends RuntimeException {
    private final String reason;

    public DeleteFailedException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
