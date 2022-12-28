package kr.nanoit.db;

import kr.nanoit.db.auth.MessageService;
import kr.nanoit.db.auth.MessageServiceImpl;
import kr.nanoit.db.query.CreateTable;
import kr.nanoit.db.query.MessageServicePostgreSqlQuerys;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@Testcontainers
class MessageServiceImplTest {

    private PostgreSqlDbcp dbcp;

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @BeforeEach
    void setUp() throws ClassNotFoundException {
        dbcp = new PostgreSqlDbcp(getDataBaseConfig());
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE IF EXISTS member,agent, message_type,message_status,access_list,agent_status");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @DisplayName("memebr 테이블이 만들어져야 한다")
    @Test
    void t1() {
        try (Connection connection = dbcp.getConnection()) {

            // given
            PreparedStatement preparedStatement = connection.prepareStatement(CreateTable.createMemberTable);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement("SELECT 1 FROM member");

            // then
            boolean actual = preparedStatement.execute();

            // then
            assertThat(actual).isTrue();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("agent 테이블이 만들어져야 한다")
    @Test
    void t2() {
        try (Connection connection = dbcp.getConnection()) {
            // given

            PreparedStatement preparedStatement = connection.prepareStatement(CreateTable.createAgentTable);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("SELECT * FROM agent");

            // when
            boolean expected = preparedStatement.execute();

            // then
            assertThat(expected).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("access_list 테이블이 만들어져야 한다")
    @Test
    void t3() {
        try (Connection connection = dbcp.getConnection()) {
            // given
            PreparedStatement preparedStatement = connection.prepareStatement(CreateTable.createAccessListTable);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement("SELECT * FROM access_list");

            // when
            boolean expected = preparedStatement.execute();

            // then
            assertThat(expected).isTrue();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("clientMessage 테이블이 만들어져야 한다")
    @Test
    void t4() {
        try (Connection connection = dbcp.getConnection()) {
            // given
            PreparedStatement preparedStatement = connection.prepareStatement(CreateTable.createClientMessageTable);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement("SELECT * FROM client_message");

            // when
            boolean expected = preparedStatement.execute();

            // then
            assertThat(expected).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("companyMessage 테이블이 만들어져야 한다")
    @Test
    void t5() {
        try (Connection connection = dbcp.getConnection()) {
            // given
            PreparedStatement preparedStatement = connection.prepareStatement(CreateTable.createCompanyMessageTable);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement("SELECT * FROM company_message");

            // when
            boolean expected = preparedStatement.execute();

            // then
            assertThat(expected).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("relayCompany 테이블이 만들어져야 한다")
    @Test
    void t6() {
        try (Connection connection = dbcp.getConnection()) {
            // given
            PreparedStatement preparedStatement = connection.prepareStatement(CreateTable.createRelayCompanyTable);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement("SELECT * FROM relay_company");

            // when
            boolean expected = preparedStatement.execute();

            // then
            assertThat(expected).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("member => findUser Method를 실행 하였을때 user가 return 되어야 한다")
    @Test
    void t7() throws SQLException {
        try (Connection connection = dbcp.getConnection()) {
            // given
            createTable(CreateTable.createMemberTable);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            MemberEntity expected = new MemberEntity(1, "이정섭", "123123", "test@test.com", timestamp, timestamp);

            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertUser(expected));
            int insertDataReuslt = preparedStatement.executeUpdate();

            // when
            MessageService messageService = new MessageServiceImpl(dbcp);
            MemberEntity actual = messageService.findUser("이정섭");

            // then
            assertThat(insertDataReuslt).isEqualTo(1);
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected);


        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    @DisplayName("member => saveUser Method를 실행 하였을때 userDto가 DB에 저장되어야 한다")
    @Test
    void t8() throws SQLException {
        // given
        createTable(CreateTable.createMemberTable);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        MemberEntity expected = new MemberEntity(1, "가정섭", "123123", "test@test.com", timestamp, timestamp);

        // when
        MessageService messageService = new MessageServiceImpl(dbcp);
        boolean insertResult = messageService.insertUser(expected);

        // then
        MemberEntity actual = messageService.findUser("가정섭");
        assertThat(insertResult).isTrue();
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getEamil()).isEqualTo(expected.getEamil());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertThat(actual.getCreated_at()).isEqualTo(expected.getCreated_at());
        assertThat(actual.getLast_modified_at()).isEqualTo(expected.getLast_modified_at());

    }

    @DisplayName("agent => findAgent Method를 실행 하였을때 agent 정보가 return 되어야 한다")
    @Test
    void t9() throws SQLException {
        try (Connection connection = dbcp.getConnection()) {
            // given
            createTable(CreateTable.createAgentTable);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            AgentEntity expected = new AgentEntity(1, 1, 1, "CONNECTED", timestamp, timestamp);
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertAgent(expected));
            int insertData = preparedStatement.executeUpdate();

            // when
            MessageService messageService = new MessageServiceImpl(dbcp);
            AgentEntity actual = messageService.findAgent(1);

            // then
            assertThat(insertData).isEqualTo(1);
            assertThat(expected).usingRecursiveComparison().isEqualTo(actual);

        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    @DisplayName("agent => updateAgentStatus Method를 실행 하였을 때 status가 변경 되어야한다")
    @Test
    void t10() {
        // given
        createTable(CreateTable.createAgentTable);

        MessageService messageService = new MessageServiceImpl(dbcp);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AgentEntity expected = new AgentEntity(2, 1, 1, "CONNECTED", timestamp, timestamp);
        boolean insertData = messageService.insertAgent(expected);

        // when
        messageService.updateAgentStatus(2, 1, "UNCONNECTED", new Timestamp(System.currentTimeMillis()));
        String actual = messageService.findAgent(2).getStatus();

        // then
        assertThat(insertData).isTrue();
        assertThat(actual).isNotEqualTo(expected.getStatus());
        assertThat(actual).isEqualTo("UNCONNECTED");
    }

    @DisplayName("agent => insertAgent Method를 실행 하였을 때 DB에 저장되어야 한다")
    @Test
    void t11() {
        // given
        createTable(CreateTable.createAgentTable);

        MessageService messageService = new MessageServiceImpl(dbcp);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        AgentEntity expected = new AgentEntity(3, 1, 1, "CONNECTED", timestamp, timestamp);

        // when
        boolean insertDataResult = messageService.insertAgent(expected);

        // then2
        assertThat(insertDataResult).isTrue();
        assertThat(messageService.findAgent(expected.getId())).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("agent => member 테이블에 없는 id값을 가진 agent를 insert 할 시 RuntimeExcpetion ")
    @Test
    void t12() {
        assertThatThrownBy(() -> {
            createTable(CreateTable.createMemberTable);
            createTable(CreateTable.createAgentTable);
            createTable(CreateTable.constraintsAgentMember_id);

            MessageService messageService = new MessageServiceImpl(dbcp);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            MemberEntity memberData = new MemberEntity(23, "양선호", "123123", "test@test.com", timestamp, timestamp);
            AgentEntity agentData = new AgentEntity(11, 3, 1, "CONNECTED", timestamp, timestamp);

            messageService.insertUser(memberData);
            messageService.insertAgent(agentData);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("org.postgresql.util.PSQLException: ERROR: insert or update on table \"agent\" violates foreign key constraint \"agent_member_id_fkey\"\n" +
                        "  Detail: Key (member_id)=(3) is not present in table \"member\".");
    }


    @DisplayName("agent => access_list에 없는 id를 가진 agent를 insert할 시 RuntimeException 발생")
    @Test
    void t13() {
        try (Connection connection = dbcp.getConnection()) {
            assertThatThrownBy(() -> {
                createTable(CreateTable.createAccessListTable);
                createTable(CreateTable.createAgentTable);
                createTable(CreateTable.constraintsAgentAccess_list_id);

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String query = "INSERT INTO access_list (id,address) VALUES (1, '192.168.0.2')";
                MessageService messageService = new MessageServiceImpl(dbcp);

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                AgentEntity expected = new AgentEntity(10, 1, 5, "CONNECTED", timestamp, timestamp);

                preparedStatement.executeUpdate();
                messageService.insertAgent(expected);
            }).isInstanceOf(RuntimeException.class)
                    .hasMessage("org.postgresql.util.PSQLException: ERROR: insert or update on table \"agent\" violates foreign key constraint \"agent_access_list_id_fkey\"\n" +
                            "  Detail: Key (access_list_id)=(5) is not present in table \"access_list\".");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("agent => agent_status에 등록되어 있는 상태 외 상태값을 넣었을 경우 RuntimeException이 발생")
    @Test
    void t14() {
        assertThatThrownBy(() -> {
            createTable(CreateTable.createAgentStatusTable);
            createTable(CreateTable.createAgentTable);
            createTable(CreateTable.constraintsAgentStatus);

            String query = "INSERT INTO agent_status (status) VALUES ('CONNECTED'),('DISCONNECTED')";
            Connection connection = dbcp.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();

            MessageService messageService = new MessageServiceImpl(dbcp);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            AgentEntity agentData = new AgentEntity(11, 3, 1, "난 멋져", timestamp, timestamp);

            messageService.insertAgent(agentData);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("org.postgresql.util.PSQLException: ERROR: insert or update on table \"agent\" violates foreign key constraint \"agent_status_fkey\"\n" +
                        "  Detail: Key (status)=(난 멋져) is not present in table \"agent_status\".");
    }

    @DisplayName("access_list => access_list_id를 제공하였을 때 데이터가 있을 경우 true가 되어야 함")
    @Test
    void t15() throws SQLException {
        try (Connection connection = dbcp.getConnection()) {
            // given
            createTable(CreateTable.createAccessListTable);
            String query = "INSERT INTO access_list (id,address) VALUES (1, '192.168.0.2')";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int insertData = preparedStatement.executeUpdate();

            // when
            MessageService messageService = new MessageServiceImpl(dbcp);
            boolean actual = messageService.isValidAccess(1);

            // then
            assertThat(insertData).isEqualTo(1);
            assertThat(actual).isTrue();


        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    @DisplayName("client_message => id값으로 select된 ClientMessae가 있을 경우 ClientMessage")
    @Test
    void t16() {

    }

    @DisplayName("")
    @Test
    void t17() {

    }

    @DisplayName("")
    @Test
    void t18() {

    }

    @DisplayName("")
    @Test
    void t19() {

    }

    @DisplayName("")
    @Test
    void t20() {

    }

    @DisplayName("")
    @Test
    void t21() {

    }

    private void createTable(String query) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static DataBaseConfig getDataBaseConfig() {
        return new DataBaseConfig()
                .setIp(postgreSQLContainer.getHost())
                .setPort(postgreSQLContainer.getFirstMappedPort())
                .setDatabaseName(postgreSQLContainer.getDatabaseName())
                .setUsername(postgreSQLContainer.getUsername())
                .setPassword(postgreSQLContainer.getPassword());
    }
}