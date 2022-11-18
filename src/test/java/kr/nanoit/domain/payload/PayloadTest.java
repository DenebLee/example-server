package kr.nanoit.domain.payload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.MetaData;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class PayloadTest {
    @Test
    void type_test() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        // ReadStream
        String input = "{\"type\": \"AUTHENTICATION\", \"data\": {\"username\": \"ppzxc\", \"password\": \"fasdfasdf\"}}";

        // RreadStream to Mapper
        InternalDataMapper internalDataMapper = new InternalDataMapper(new MetaData("asdf"), input);
        System.out.println("INTERNAL DATA MAPPER CLASS");
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(internalDataMapper));

        // in Mapper
        Payload originalPayload = objectMapper.readValue(internalDataMapper.getPayload(), Payload.class);
        System.out.println(originalPayload.getData().getClass().getName());

        InternalDataFilter testFilter = new InternalDataFilter(internalDataMapper.getMetaData(), originalPayload);

        // MAPPER LOGIC
        InternalDataFilter mappedFilter = null;
        if (originalPayload.getType().equals(PayloadType.AUTHENTICATION)) {
//            System.out.println(((LinkedHashMap) originalPayload.getData()).get("username"));

            Authentication authentication = objectMapper.convertValue(originalPayload.getData(), Authentication.class);

            Payload mappedPayload = new Payload(originalPayload.getType(), "TEST1234", authentication);
            System.out.println(mappedPayload.getData().getClass().getName());

            System.out.println("MAPPED PAYLOAD");
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mappedPayload));
            mappedFilter = new InternalDataFilter(internalDataMapper.getMetaData(), mappedPayload);
            System.out.println("TO FILTER OBJECT");
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mappedFilter));
        } else if (originalPayload.getType().equals(PayloadType.SEND)) {
//            objectMapper.convertValue(originalPayload.getData(), Message.class);
        } else {
            new RuntimeException("NOT FOUND"); // outbound로 보냄
        }











        // IN FILTER
        if (mappedFilter.getPayload().getData() instanceof Authentication) {
            System.out.println("authentication!!");
            Authentication authentication = (Authentication) mappedFilter.getPayload().getData();

            System.out.println(authentication);
        } else if (mappedFilter.getPayload().getData() instanceof HashMap) {
            System.out.println("hashmap!!");
        }
    }


    @Test
    void name() throws JsonProcessingException {
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(new Payload()));
    }
}