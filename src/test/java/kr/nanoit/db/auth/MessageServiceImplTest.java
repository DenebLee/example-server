package kr.nanoit.db.auth;

import kr.nanoit.db.DataBaseConfig;
import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.query.CreateTable;
import kr.nanoit.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Testcontainers
class MessageServiceImplTest {

    private PostgreSqlDbcp dbcp;
    private MessageService authService;

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @BeforeEach
    void setUp() throws ClassNotFoundException {
        dbcp = new PostgreSqlDbcp(getDataBaseConfig());
        authService = new MessageServiceImpl(dbcp);
        createTable();
    }


    @DisplayName("memebr 테이블이 만들어져야 한다")
    @Test
    void t1() {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1 FROM member");
            ResultSet resultSet = preparedStatement.executeQuery();

            assertThat(resultSet.getMetaData().getColumnCount()).isGreaterThanOrEqualTo(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("member 테이블에 insert 했을때 성공적으로 insert 해야 한다")
    @Test
    void t2() throws SQLException {
        // given
        String dateTime = "2020-12-12 01:24:23";
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        UserDto userDto = new UserDto();
        userDto.setUsername("이정섭")
                .setPassword("123")
                .setEmail("test@test.com")
                .setCreated_at(timestamp)
                .setLast_modified_at(timestamp);


        // when
        boolean actual = authService.saveUser(userDto);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("")
    @Test
    void t3() {

    }

    @DisplayName("")
    @Test
    void t4() {

    }

    @DisplayName("")
    @Test
    void t5() {

    }

    private void createTable() {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(CreateTable.createMemberTable);
            preparedStatement.executeUpdate();
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