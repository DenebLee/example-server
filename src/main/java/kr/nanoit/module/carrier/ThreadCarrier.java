package kr.nanoit.module.carrier;


import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.domain.broker.InternalDataCarrier;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.dto.ClientMessageDto;
import kr.nanoit.dto.CompanyMessageDto;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

/*
    무조건 받으면 성공
    5초마다 Report
*/
@Slf4j
public class ThreadCarrier extends ModuleProcess {
    private final Broker broker;
    private final MessageService messageService;
    private boolean flag;


    public ThreadCarrier(Broker broker, String uuid, MessageService messageService) {
        super(broker, uuid);
        this.broker = broker;
        this.messageService = messageService;
    }

    @Override
    public void run() {
        try {
            flag = true;
            while (true) {
                Object object = broker.subscribe(InternalDataType.CARRIER);
                if (object instanceof InternalDataCarrier) {
                    InternalDataCarrier internalDataCarrier = (InternalDataCarrier) object;
                    if (internalDataCarrier.getPayload().getData() instanceof ClientMessageDto) {
                        ClientMessageDto clientMessageDto = (ClientMessageDto) internalDataCarrier.getPayload().getData();
                        if (clientMessageDto != null) {
                            CompanyMessageDto companyMessageDto = makeCompanyMessageDto(clientMessageDto);
                            if (messageService.insertCompanyMessage(companyMessageDto.toEntity())) {


                                // 7초에 한번씩 레포트 전송해줘야 함
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        flag = false;
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
}
