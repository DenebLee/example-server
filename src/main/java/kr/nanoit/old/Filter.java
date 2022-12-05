package kr.nanoit.old;

import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.ErrorDto;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * InBound메시지를 유효성 검사만 수행
 * <p>
 * * - 성공 -> Branch로 전송
 * * - 실패 -> 아웃바운드로 ( 실패 메시지를 Client 로 전송 해야됨 )
 */
@Slf4j
public class Filter implements Process {
    private final Broker broker;
    private boolean flag;
    private Instant start;
    private Instant finish;

    public Filter(Broker broker) {
        this.broker = broker;
    }

    @Override
    public void run() {
        try {
            start = Instant.now();
            flag = true;

            while (flag) {
                Object object = broker.subscribe(InternalDataType.FILTER);
                if (object != null && object instanceof InternalDataFilter) {
//                    log.info("[FILTER]   DATA INPUT => [{}]", object);
                    InternalDataFilter internalDataFilter = (InternalDataFilter) object;

                    if (internalDataFilter.getMetaData() != null && internalDataFilter.getPayload().getData() != null) {
                        if (broker.publish(new InternalDataBranch(internalDataFilter.getMetaData(), new Payload(internalDataFilter.getPayload().getType(), internalDataFilter.getPayload().getMessageUuid(), internalDataFilter.getPayload().getData())))) {
//                            log.info("[FILTER]   TO BRANCH => [TYPE : {} DATA : {}]", internalDataFilter.getMetaData(), internalDataFilter.getPayload().getData());
                        }
                    } else {
                        if (broker.publish(new InternalDataOutBound(internalDataFilter.getMetaData(), new Payload(PayloadType.BAD_SEND, internalDataFilter.getPayload().getMessageUuid(), new ErrorDto("Data null"))))) {
                            log.error("[FILTER]   There is null data ");
                        }
                    }
                }
            }
            finish = Instant.now();
        } catch (InterruptedException e) {
            flag = false;
            e.printStackTrace();
        }
    }

    @Override
    public String getUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    @Override
    public boolean getFlag() {
        return this.flag;
    }

    @Override
    public long getRunningTime() {
        return Duration.between(start, finish).toMillis();
    }
}
