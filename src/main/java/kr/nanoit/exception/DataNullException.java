package kr.nanoit.exception;

import kr.nanoit.domain.broker.InternalData;
import kr.nanoit.domain.broker.InternalDataFilter;

public class DataNullException extends RuntimeException {
    private final String reason;
    private final InternalData internalData;

    public DataNullException(InternalData internalData, String reason) {
        super(reason);
        this.reason = reason;
        this.internalData = internalData;
    }

    public String getReason() {
        return reason;
    }

    public InternalData getInternalData() {
        return internalData;
    }
}
