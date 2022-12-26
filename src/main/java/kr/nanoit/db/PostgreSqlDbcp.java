package kr.nanoit.db;


import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PostgreSqlDbcp {

    private final DataSource pool;

    public PostgreSqlDbcp(DataBaseConfig config) throws ClassNotFoundException {
        String connectUrl = String.format("jdbc:postgresql://%s:%d/%s", config.getIp(), config.getPort(), config.getDatabaseName());
        this.pool = setDataSource(connectUrl, config.getUsername(), config.getPassword());
    }

    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    public DataSource setDataSource(String connectURI, String username, String password) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver"); // 드라이버 로딩
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, username, password);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        // 실질적인 커넥션을 보관하고 있으며 커넥션 풀을 관리하는데 필요한 기능을 추가적으로 제공
        ObjectPool<PoolableConnection> connectionObjectPool = new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionObjectPool);
        return new PoolingDataSource<>(connectionObjectPool);
    }
}
