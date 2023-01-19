package kr.nanoit.exception;

import kr.nanoit.domain.broker.InternalDataBranch;

public class ValidationException extends RuntimeException {
    private final String reason;
    private final InternalDataBranch internalDataBranch;

    public ValidationException(InternalDataBranch internalDataBranch, String reason) {
        super(reason);
        this.reason = reason;
        this.internalDataBranch = internalDataBranch;
    }

    public String getReason() {
        return reason;
    }

    public InternalDataBranch getInternalDataBranch() {
        return internalDataBranch;
    }
}
