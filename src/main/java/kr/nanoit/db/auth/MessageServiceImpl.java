package kr.nanoit.db.auth;

import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.query.MessageServicePostgreSqlQuerys;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.dto.UserDto;

import java.sql.*;

public class MessageServiceImpl implements MessageService {
    private final PostgreSqlDbcp dbcp;

    public MessageServiceImpl(PostgreSqlDbcp dbcp) {
        this.dbcp = dbcp;
    }

    @Override
    public MemberEntity findUser(String username) throws SQLException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.findUser(username));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    MemberEntity user = new MemberEntity();
                    user.setId(resultSet.getLong("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEamil(resultSet.getString("email"));
                    user.setCreated_at(resultSet.getTimestamp("create_at"));
                    user.setLast_modified_at(resultSet.getTimestamp("last_modified_at"));
                    return user;
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public boolean saveUser(UserDto userDto) throws SQLException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertUser(userDto));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            } else if (result == 0) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean containsById() {
        return false;
    }

    @Override
    public AgentEntity findAgent(long agentId) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.findAgent(agentId));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    AgentEntity agentEntity = new AgentEntity();
                    agentEntity.setAgent_id(resultSet.getLong("id"));
                    agentEntity.setMemeber_id(resultSet.getLong("member_id"));
                    agentEntity.setAccess_list_id(resultSet.getLong("access_list_id"));
                    agentEntity.setStatus(resultSet.getString("status"));
                    agentEntity.setCreated_at(resultSet.getTimestamp("created_at"));
                    agentEntity.setLast_modified_at(resultSet.getTimestamp("last_modified_at"));
                    return agentEntity;
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateAgentStatus(long memeberId, String status) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.updateAgentStatus(memeberId, status));
            if (preparedStatement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean isValidAccess(long accessListId) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.findAccessList(accessListId));
            return preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
