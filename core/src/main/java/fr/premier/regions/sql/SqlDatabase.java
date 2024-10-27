package fr.premier.regions.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SqlDatabase {
    protected final HikariDataSource source;

    public SqlDatabase(SqlCredential credential) {
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName(credential.getDriverType().getDriverClassName());
        config.setJdbcUrl(String.format(credential.buildUrl(), credential.getHost(), credential.getPort(), credential.getDatabase()));
        config.setUsername(credential.getUser());
        config.setPassword(credential.getPassword());
        config.setMaximumPoolSize(credential.getPoolSize());
        config.setConnectionTimeout(30_000);
        this.source = new HikariDataSource(config);
    }

    protected Connection getConnection() {
        try {
            return this.source.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot get SQL connection", e);
        }
    }

    public void openConnection(SqlConsumer<Connection> consumer) {
        try (final Connection connection = this.getConnection()) {
            consumer.accept(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> V resultPreparedStatement(String sql, SqlResultStatement<PreparedStatement, V> consumer) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(sql)) {
                return consumer.result(statement);
            }
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> V resultStatement(SqlResultStatement<Statement, V> consumer) {
        try (final Connection connection = this.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                return consumer.result(statement);
            }
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public final void createClosingStatement(SqlStatement<Statement> consumer) {
        try (final Connection connection = this.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                consumer.execute(statement);
            }
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public final void prepareClosingStatement(String preparedStatement, SqlStatement<PreparedStatement> consumer) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(preparedStatement)) {
                consumer.execute(statement);
            }
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
