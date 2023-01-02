package kr.nanoit.exception;

import java.sql.SQLException;

public class FindFailedException extends RuntimeException {
    private final String reason;

    public FindFailedException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
