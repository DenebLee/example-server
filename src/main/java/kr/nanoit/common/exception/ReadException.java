package kr.nanoit.common.exception;

public class ReadException extends Exception {

    private final String reason;
    private final String uuid;

    public ReadException(String reason, String uuid) {
        super(reason);
        this.reason = reason;
        this.uuid = uuid;
    }

    public String getReason() {
        return reason;
    }

    public String getUuid() {
        return uuid;
    }
}