package kr.nanoit.exception;

import kr.nanoit.domain.broker.InternalData;
import kr.nanoit.domain.broker.InternalDataFilter;

public class DataNullException extends RuntimeException {
    private final String reason;

    public DataNullException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
