package guru.qa.niffler.data.tpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class JdbcConnectionHolder implements AutoCloseable {

    private final DataSource dataSource;
    private final Map<Long, Connection> connection = new ConcurrentHashMap<Long, Connection>();

    public JdbcConnectionHolder(@Nonnull DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Nonnull
    public Connection connection() {
        return connection.computeIfAbsent(Thread.currentThread().threadId(),
                key -> {
                    try {
                        return dataSource.getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void close() {
        @Nullable Connection conn = connection.remove(Thread.currentThread().threadId());
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                //NOP
            }
        }
    }

    public void closeAllConnections() {
        connection.values().forEach(connection -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                //NOP
            }
        });
    }
}