package kr.nanoit.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.payload.Send;

import java.util.regex.Pattern;

public class Validation {
    private final ObjectMapper objectMapper;

    public Validation() {
        this.objectMapper = new ObjectMapper();
    }

    public boolean verificationSendData(InternalDataFilter internalDataFilter) {
        Send send = objectMapper.convertValue(internalDataFilter.getPayload().getData(), Send.class);
        if (send.getId() == 0 || send.getId() < 0) {
            return false;
        }
        if (!isPhoneNum(send.getPhone()) || send.getPhone() == null || send.getPhone().contains(" ") || send.getPhone().equals("")) {
            return false;
        }
        if (!isCallBackByPhone(send.getCallback()) && isCallBackByRegularTelephoneNum(send.getCallback()) || send.getCallback() == null || send.getCallback().contains(" ") || send.getCallback().equals("")) {
            return false;
        }
        if (send.getContent() == null || send.getContent().equals("")) {
            return false;
        }
        return true;
    }
    
    // 기능 구현전

    public boolean verificationAliveData(InternalDataFilter internalDataFilter) {
        return true;
    }

    public boolean verificationReport_ackData(InternalDataFilter internalDataFilter) {
        return true;
    }

    private boolean isPhoneNum(String num) {
        return Pattern.matches("^\\d{2,3}\\d{3,4}\\d{4}$", num);
    }

    private boolean isCallBackByPhone(String str) {
        return Pattern.matches("^\\d{2,3}\\d{3,4}\\d{4}$", str);
    }

    private boolean isCallBackByRegularTelephoneNum(String str) {
        return Pattern.matches("^\\d{2,3} - \\d{3,4} - \\d{4}$", str);
    }
}
