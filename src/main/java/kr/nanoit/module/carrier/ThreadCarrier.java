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
import kr.nanoit.exception.DataNullException;
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

                        if (clientMessageDto == null) {
                            throw new DataNullException("The value received does not exist");
                        }

                        CompanyMessageDto companyMessageDto = makeCompanyMessageDto(clientMessageDto);

                        if (messageService.insertCompanyMessage(companyMessageDto.toEntity())) {
                            if (broker.publish(new InternalDataOutBound(internalDataCarrier.getMetaData(), new Payload(PayloadType.REPORT, internalDataCarrier.getPayload().getMessageUuid(), new Report(clientMessageDto.getAgent_id(), MessageResult.SUCCESS))))) {
                                log.debug("[CARRIER]   DATA TO OUTBOUND => [TYPE : {} DATA : {}]", internalDataCarrier.getPayload().getType(), internalDataCarrier.getPayload());
                            }
                        }
                    }

                }
            } catch (DataNullException e) {
                receiveResult(e.getReason(), internalDataCarrier);
                log.warn("[CARRIER] @USER:{}] DataNullException Call  {} ", internalDataCarrier.UUID(), e.getReason());
            } catch (InsertFailedException e) {
                e.printStackTrace();
                receiveResult(e.getReason(), internalDataCarrier);
                log.warn("[CARRIER] @USER:{}] DataNullException Call  {} ", internalDataCarrier.UUID(), e.getReason());
            } catch (Exception e) {
                e.printStackTrace();
                receiveResult("unknown Error", internalDataCarrier);
            }
        }
    }

    private CompanyMessageDto makeCompanyMessageDto(ClientMessageDto clientMessageDto) {
        CompanyMessageDto companyMessageDto = new CompanyMessageDto();
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

    private void receiveResult(String reason, InternalDataCarrier internalDataCarrier) {
        broker.publish(new InternalDataOutBound(internalDataCarrier.getMetaData(), new Payload(PayloadType.REPORT, internalDataCarrier.getPayload().getMessageUuid(), new ErrorPayload(reason))));
    }
}
