package kr.nanoit.module.sender;

import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.domain.broker.InternalDataCarrier;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataSender;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.message.MessageResult;
import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.domain.payload.*;
import kr.nanoit.dto.ClientMessageDto;
import kr.nanoit.exception.FindFailedException;
import kr.nanoit.exception.InsertFailedException;
import kr.nanoit.exception.UpdateFailedException;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

@Slf4j
public class ThreadSender extends ModuleProcess {

    private final MessageService messageService;
    private InternalDataSender internalDataSender;

    public ThreadSender(Broker broker, String uuid, MessageService messageService) {
        super(broker, uuid);
        this.messageService = messageService;
    }

    @Override
    public void run() {
        try {
            this.flag = true;
            while (this.flag) {
                Object object = broker.subscribe(InternalDataType.SENDER);
                if (object != null && object instanceof InternalDataSender) {
                    internalDataSender = (InternalDataSender) object;
                    Send send = (Send) internalDataSender.getPayload().getData();

                    if (send == null || send.getContent().isEmpty() || send.getSender_num().isEmpty() || send.getSender_name().isEmpty() || send.getSender_callback().isEmpty() || send.getAgent_id() == 0) {
                        sendResult("SendMessage is null", internalDataSender, new Exception());
                    } else {
                        ClientMessageDto messageDto = makeMessage(send);
                        long id = messageService.insertClientMessage(messageDto.toEntity());
                        messageDto.setId(id);
                        // Carrier 로 보낼때는 Payload에 ClinetMessageDto 적재
                        if (broker.publish(new InternalDataCarrier(internalDataSender.getMetaData(), new Payload(PayloadType.SEND_ACK, internalDataSender.getPayload().getMessageUuid(), messageDto)))) {
                            // 전송 완료 시 status receive -> sent로 업데이트 후
                            if (messageService.updateMessageStatus(id, MessageStatus.SENT)) {
                                // 업데이트 성공 완료되면 outBound로 전송
                                if (broker.publish(new InternalDataOutBound(internalDataSender.getMetaData(), new Payload(PayloadType.SEND_ACK, internalDataSender.getPayload().getMessageUuid(), new SendAck(MessageResult.SUCCESS)))))
                                    ;
                            }
                        }
                    }

                }
            }
        } catch (InsertFailedException e) {
            sendResult(e.getReason(), internalDataSender, e);
        } catch (FindFailedException e) {
            sendResult(e.getReason(), internalDataSender, e);
        } catch (UpdateFailedException e) {
            sendResult(e.getReason(), internalDataSender, e);
        } catch (Exception e) {
            e.printStackTrace();
            sendResult("unknown Error", internalDataSender, e);
            shoutDown();
        }

    }

    private void sendResult(String reason, InternalDataSender internalDataSender, Exception exception) {
        if (broker.publish(new InternalDataOutBound(internalDataSender.getMetaData(), new Payload(PayloadType.SEND_ACK, internalDataSender.getPayload().getMessageUuid(), new ErrorPayload(reason))))) {
            log.warn("[SENDER]   key = {} reason = {}", internalDataSender.getMetaData().getSocketUuid(), reason, exception);
        }
    }

    @Override
    public void shoutDown() {
        this.flag = false;
        log.warn("[SENDER]   THIS THREAD SHUTDOWN");
    }

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    private ClientMessageDto makeMessage(Send send) {
        ClientMessageDto clientMessageDto = new ClientMessageDto();
        clientMessageDto.setId(0);
        clientMessageDto.setAgent_id(send.getAgent_id());
        clientMessageDto.setType(PayloadType.SEND);
        clientMessageDto.setStatus(MessageStatus.RECEIVE);
        clientMessageDto.setSend_time(new Timestamp(System.currentTimeMillis()));
        clientMessageDto.setSender_num(send.getSender_num());
        clientMessageDto.setSender_callback(send.getSender_callback());
        clientMessageDto.setSender_name(send.getSender_name());
        clientMessageDto.setContent(send.getContent());
        clientMessageDto.setCreated_at(new Timestamp(System.currentTimeMillis()));
        clientMessageDto.setLast_modified_at(new Timestamp(System.currentTimeMillis()));

        return clientMessageDto;
    }
}

//
//