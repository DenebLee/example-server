package kr.nanoit.db.auth;

import com.google.inject.spi.Message;
import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.query.MessageServicePostgreSqlQuerys;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.ClientMessageEntity;
import kr.nanoit.domain.entity.CompanyMessageEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.exception.DeleteFailedException;
import kr.nanoit.exception.FindFailedException;
import kr.nanoit.exception.InsertFailedException;
import kr.nanoit.exception.UpdateFailedException;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class MessageServiceImpl implements MessageService {
    private final PostgreSqlDbcp dbcp;

    public MessageServiceImpl(PostgreSqlDbcp dbcp) {
        this.dbcp = dbcp;
    }

    @Override
    public MemberEntity findUser(String username) throws FindFailedException {
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
                    user.setCreated_at(resultSet.getTimestamp("created_at"));
                    user.setLast_modified_at(resultSet.getTimestamp("last_modified_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            log.error("failed to find user", e);
            throw new FindFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertUser(MemberEntity memberEntity) throws InsertFailedException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertUser(memberEntity));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            } else if (result == 0) {
                return false;
            }
        } catch (SQLException e) {
            log.error("failed to insert Member Info");
            e.printStackTrace();
            throw new InsertFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean containsById() {
        return false;
    }

    @Override
    public AgentEntity findAgent(long id) throws FindFailedException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.findAgent(id));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    AgentEntity agentEntity = new AgentEntity();
                    agentEntity.setId(resultSet.getLong("id"));
                    agentEntity.setMember_id(resultSet.getLong("member_id"));
                    agentEntity.setAccess_list_id(resultSet.getLong("access_list_id"));
                    agentEntity.setStatus(AgentStatus.valueOf(resultSet.getString("status")));
                    agentEntity.setCreated_at(resultSet.getTimestamp("created_at"));
                    agentEntity.setLast_modified_at(resultSet.getTimestamp("last_modified_at"));
                    return agentEntity;
                }
            }
        } catch (SQLException e) {
            log.error("failed to find Agent", e);
            throw new FindFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertAgent(AgentEntity agentEntity) throws InsertFailedException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertAgent(agentEntity));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            } else if (result == 0) {
                return false;
            }
        } catch (SQLException e) {
            log.error("failed to insert Agent", e);
            throw new InsertFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateAgentStatus(long id, long memeberId, AgentStatus status, Timestamp updateTime) throws UpdateFailedException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.updateAgentStatus(id, memeberId, status, updateTime));
            if (preparedStatement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            log.error("failed to update Agent Status", e);
            throw new UpdateFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isValidAccess(long accessListId) throws RuntimeException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.findAccessList(accessListId));
            return preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ClientMessageEntity findClientMessage(long id) throws FindFailedException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.findClientMessage(id));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    ClientMessageEntity clientMessageEntity = new ClientMessageEntity();
                    clientMessageEntity.setId(resultSet.getLong("id"));
                    clientMessageEntity.setAgent_id(resultSet.getLong("agent_id"));
                    clientMessageEntity.setStatus(MessageStatus.valueOf(resultSet.getString("status")));
                    clientMessageEntity.setType(PayloadType.valueOf(resultSet.getString("type")));
                    clientMessageEntity.setSend_time(resultSet.getTimestamp("send_time"));
                    clientMessageEntity.setSender_num(resultSet.getString("sender_num"));
                    clientMessageEntity.setSender_name(resultSet.getString("sender_name"));
                    clientMessageEntity.setSender_callback(resultSet.getString("sender_callback"));
                    clientMessageEntity.setContent(resultSet.getString("content"));
                    clientMessageEntity.setCreated_at(resultSet.getTimestamp("created_at"));
                    clientMessageEntity.setLast_modified_at(resultSet.getTimestamp("last_modified_at"));
                    return clientMessageEntity;
                }
            }
            return null;
        } catch (SQLException e) {
            log.error("failed to find message", e);
            throw new FindFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteClientMessage(long id) throws DeleteFailedException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.deleteClientMessage());
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("failed to delete message", e);
            throw new DeleteFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long insertClientMessage(ClientMessageEntity clientMessageEntity) throws InsertFailedException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertClientMessage(clientMessageEntity), Statement.RETURN_GENERATED_KEYS);
            int result = preparedStatement.executeUpdate();

            if (result == 1) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                while (rs.next()) {
                    return rs.getLong(1);
                }
                rs.close();
            }
        } catch (SQLException e) {
            log.error("failed to insert Client Message");
            throw new InsertFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean updateMessageStatus(long id, MessageStatus messageStatus) throws UpdateFailedException {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.updateMessageStatus(id, messageStatus));
            if (preparedStatement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException e) {
            log.error("failed to update Client Message");
            throw new UpdateFailedException(e.getMessage());
        }
        return false;
    }

    @Override
    public CompanyMessageEntity findCompanyMessage() {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.findCompanyMessage());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteCompanyMessage() {
        try (Connection connection = dbcp.getConnection()) {

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateCompanyMessage() {
        try (Connection connection = dbcp.getConnection()) {

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertCompanyMessage(CompanyMessageEntity companyMessageEntity) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertCompanyMessage(companyMessageEntity));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new InsertFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertAccessList(long id, String address) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertAccessList(id, address));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new InsertFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateAccessList(long id, long replaceId) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.updateAccessList(id, replaceId));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new UpdateFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertAgentStatus(String status1, String status2) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertAgentStatus(status1, status2));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new InsertFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertMessageType(String type) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertMessageType(type));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new InsertFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertMessageStatus(String status1, String status2) {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertMessageStatus(status1, status2));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new InsertFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertRelayCompany() {
        try (Connection connection = dbcp.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(MessageServicePostgreSqlQuerys.insertRelayCompany(new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis())));
            int result = preparedStatement.executeUpdate();
            if (result == 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new InsertFailedException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
