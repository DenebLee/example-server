package kr.nanoit.exception;

public class InsertFailedException extends RuntimeException {
    private final String reason;

    public InsertFailedException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
