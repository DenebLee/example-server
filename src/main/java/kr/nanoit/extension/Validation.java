package kr.nanoit.extension;

import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.module.inbound.socket.UserManager;

import java.util.regex.Pattern;

public class Validation {


    public boolean verificationSendData(InternalDataFilter internalDataFilter, UserManager userManager) {
        Send send = (Send) internalDataFilter.getPayload().getData();
        if (send.getMessageNum() == 0) {
            return false;
        }
        if (!isPhoneNum(send.getPhoneNum()) || send.getPhoneNum() == null || send.getPhoneNum().contains(" ") || send.getPhoneNum().equals("")) {
            return false;
        }

        if (!isCallBackByPhone(send.getCallback()) || send.getCallback() == null || send.getCallback().contains(" ") || send.getCallback().equals("")) {
            return false;
        }

        if (send.getName() == null || send.getName().equals("") || send.getName().contains(" ") || send.getName().length() > 8) {
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
        return Pattern.matches("^\\d{3}-\\d{4}-\\d{4}$", num) || Pattern.matches("^\\d{3}\\d{4}\\d{4}$", num);
    }

    private boolean isCallBackByPhone(String str) {
        return Pattern.matches("^\\d{2,3}\\d{3,4}\\d{4}$", str) || Pattern.matches("^\\d{2,3}-\\d{3,4}-\\d{4}$", str) || Pattern.matches("^\\d{3,4}-\\d{4}$", str);
    }
}
