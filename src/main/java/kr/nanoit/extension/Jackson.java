package kr.nanoit.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// 공식 X
// JVM 특성을 이용한 방식
//
public final class Jackson {

    private final ObjectMapper objectMapper;

    private Jackson() {
       this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
//        this.objectMapper = Jackson.getInstance().getObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static Jackson getInstance() {
        return Singleton.INSTANCE;
    }

    // INNER CLASS
    // LAZY 초기화
    // INNER 클래스가 포함된 클래스를 초기화
    private static class Singleton {
        private static final Jackson INSTANCE = new Jackson();
    }
}
