package kr.nanoit.module.filter;

import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.ErrorType;
import kr.nanoit.module.borker.Broker;

/**
 * InBound메시지를 유효성 검사만 수행
 * <p>
 * * - 성공 -> Branch로 전송
 * * - 실패 -> 아웃바운드로 ( 실패 메시지를 Client 로 전송 해야됨 )
 */
public class Filter implements Runnable {
    private final Broker broker;

    public Filter(Broker broker) {
        this.broker = broker;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object object = broker.subscribe(InternalDataType.FILTER);
                InternalDataFilter internalDataFilter = (InternalDataFilter) object;
                // 유효성 검사만 실시
                if (internalDataFilter.getMetaData() != null && internalDataFilter.getPayload().getData() != null) {
                    broker.publish(internalDataFilter);
                } else {
                    InternalDataOutBound internalDataOutBound = new InternalDataOutBound();
                    internalDataOutBound.setErrorType(ErrorType.NULL);
                    internalDataOutBound.setErrorContent("널입니다 내용넣으세요");
                    broker.publish(internalDataOutBound);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
