package kr.nanoit.module.carrier;


import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.domain.broker.InternalDataCarrier;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.message.MessageResult;
import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.domain.payload.ErrorPayload;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Report;
import kr.nanoit.dto.ClientMessageDto;
import kr.nanoit.dto.CompanyMessageDto;
import kr.nanoit.exception.InsertFailedException;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

@Slf4j
public class ThreadCarrier extends ModuleProcess {
    private final Broker broker;
    private final MessageService messageService;
    private boolean flag;
    private InternalDataCarrier internalDataCarrier;


    public ThreadCarrier(Broker broker, String uuid, MessageService messageService) {
        super(broker, uuid);
        this.broker = broker;
        this.messageService = messageService;
    }

    @Override
    public void run() {
        this.flag = true;
        while (this.flag) {
            try {
                Object object = broker.subscribe(InternalDataType.CARRIER);
                if (object != null && object instanceof InternalDataCarrier) {
                    internalDataCarrier = (InternalDataCarrier) object;
                    if (internalDataCarrier.getPayload().getData() instanceof ClientMessageDto) {
                        ClientMessageDto clientMessageDto = (ClientMessageDto) internalDataCarrier.getPayload().getData();
                        if (clientMessageDto != null) {
                            CompanyMessageDto companyMessageDto = makeCompanyMessageDto(clientMessageDto);
                            if (messageService.insertCompanyMessage(companyMessageDto.toEntity())) {
                                Thread.sleep(100);
                                broker.publish(new InternalDataOutBound(internalDataCarrier.getMetaData(), new Payload(PayloadType.REPORT, internalDataCarrier.getPayload().getMessageUuid(), new Report(clientMessageDto.getAgent_id(), MessageResult.SUCCESS))));
                            }
                        }
                    }
                }
            } catch (InsertFailedException e) {
                e.printStackTrace();
                receiveResult(e.getReason(), internalDataCarrier, e);
            } catch (Exception e) {
                receiveResult("unknown Error", internalDataCarrier, e);
            }
        }

    }

    private CompanyMessageDto makeCompanyMessageDto(ClientMessageDto clientMessageDto) {
        CompanyMessageDto companyMessageDto = new CompanyMessageDto();
        // 정해진 게 없어서 realy_company 값은 1로 초기값 설정
        return companyMessageDto
                .setClient_message_id(clientMessageDto.getId())
                .setRelay_company_id(1)
                .setType(clientMessageDto.getType())
                .setStatus(MessageStatus.RECEIVE)
                .setSend_time(clientMessageDto.getSend_time())
                .setSender_num(clientMessageDto.getSender_num())
                .setSender_callback(clientMessageDto.getSender_callback())
                .setSender_name(clientMessageDto.getSender_name())
                .setContent(clientMessageDto.getContent())
                .setCreated_at(new Timestamp(System.currentTimeMillis()))
                .setLast_modified_at(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void shoutDown() {
        this.flag = false;
        log.warn("[CARRIER   THIS THREAD SHUTDOWN]");
    }

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    private void receiveResult(String reason, InternalDataCarrier internalDataCarrier, Exception exception) {
        if (broker.publish(new InternalDataOutBound(internalDataCarrier.getMetaData(), new Payload(PayloadType.REPORT, internalDataCarrier.getPayload().getMessageUuid(), new ErrorPayload(reason))))) {
            log.warn("[CARRIER]  reason = {}", reason, exception);
        }
    }
}
