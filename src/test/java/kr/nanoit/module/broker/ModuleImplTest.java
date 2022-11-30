package kr.nanoit.module.broker;

import kr.nanoit.domain.broker.*;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.module.branch.Branch;
import kr.nanoit.module.filter.Filter;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.mapper.Mapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("ModuleImpl Test")
class ModuleImplTest {
    private Broker broker;
    private ObjectMapper objectMapper;
    private SocketManager socketManager;
    private final String inputData = "{\"type\": \"SEND\", \"data\": {\"username\": \"lee\", \"password\": \"123123\"}}";
    private Thread thread;

    @BeforeEach
    void setUp() {
        socketManager = new SocketManager();
        broker = new BrokerImpl(socketManager);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        thread.interrupt();
    }

    @Test
    @DisplayName("Mapper Test")
    void when_running_mapper_should_return_Internal_data_filter() throws InterruptedException, IOException {
        // given
        InternalDataMapper expected = new InternalDataMapper();
        expected.setMetaData(new MetaData(randomString(10)));
        expected.setPayload(inputData);
        broker.publish(expected);
        Payload expectedPayload = objectMapper.readValue(expected.getPayload(), Payload.class);


        // when
        thread = new Thread(new Mapper(broker));
        Object object = broker.subscribe(InternalDataType.FILTER);
        InternalDataFilter actual = (InternalDataFilter) object;
        // Mapper 에서 큐에서 빼낸건 Mapper 이고 변환 후 큐에 넣는 형식은 Filter


        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getMetaData()).isEqualTo(expected.getMetaData());
        assertThat(expectedPayload).usingRecursiveComparison().isEqualTo(expected.getPayload());
    }

    @Test
    @DisplayName("Filter Test")
    void when_running_filter_should_return_internal_data_branch() throws IOException, InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(randomString(9)));
        expected.setPayload(objectMapper.readValue(inputData, Payload.class));

        // when
        thread = new Thread(new Filter(broker));
        Object object = broker.subscribe(InternalDataType.BRANCH);
        InternalDataBranch actual = (InternalDataBranch) object;

        // then
        assertThat(actual.getMetaData()).isEqualTo(expected.getMetaData());
//        assertThat()
    }

    @Test
    @DisplayName("Branch Test")
    void when_running_branch_return_distribute_according_to_type() throws IOException, InterruptedException {
        // given
        InternalDataBranch expected = new InternalDataBranch();
        expected.setMetaData(new MetaData(randomString(22)));
        expected.setPayload(objectMapper.readValue(inputData, Payload.class));
        broker.publish(expected);

        // when
        thread = new Thread(new Branch(broker));
        Object object = broker.subscribe(InternalDataType.BRANCH);
        InternalDataOutBound actual = (InternalDataOutBound) object;
        // then

    }

    public String randomString(int targetLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetLength);
        for (int i = 0; i < targetLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
