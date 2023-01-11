package kr.nanoit.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.module.inbound.socket.UserManager;

import java.util.regex.Pattern;

public class Validation {
    private final ObjectMapper objectMapper;

    public Validation() {
        this.objectMapper = new ObjectMapper();
    }

    public boolean verificationSendData(InternalDataFilter internalDataFilter, UserManager userManager) {
        Send send = objectMapper.convertValue(internalDataFilter.getPayload().getData(), Send.class);
        if (send.getAgent_id() == 0 || send.getAgent_id() < 0) {
            if (userManager.isExistsUserInfo(internalDataFilter.UUID(), send.getAgent_id()) == false) {
                return false;
            }
            return false;
        }
        if (!isPhoneNum(send.getSender_num()) || send.getSender_num() == null || send.getSender_num().contains(" ") || send.getSender_num().equals("")) {
            return false;
        }
        if (!isCallBackByPhone(send.getSender_callback()) || send.getSender_callback() == null || send.getSender_callback().contains(" ") || send.getSender_callback().equals("")) {
            return false;
        }
        if (send.getSender_name() == null || send.getSender_name().contains("") || send.getSender_num().contains(" ") || send.getSender_num().length() > 8) {
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
        return Pattern.matches("^\\d{3} - \\d{4} - \\d{4}$", num) || Pattern.matches("^\\d{3}\\d{4}\\d{4}$", num);
    }

    private boolean isCallBackByPhone(String str) {
        return Pattern.matches("^\\d{2,3}\\d{3,4}\\d{4}$", str) || Pattern.matches("^\\d{2,3} - \\d{3,4} - \\d{4}$", str);
    }
}
