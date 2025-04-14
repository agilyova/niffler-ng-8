package guru.qa.niffler.data;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class DataBases {
  private DataBases() {
  }

  private static final Map<String, DataSource> datasources = new ConcurrentHashMap<>();
  private static final Map<Long, Map<String, Connection>> treadConnections = new ConcurrentHashMap<>();
  private static final int DEFAULT_ISOLATION_LEVEL = TRANSACTION_READ_COMMITTED;

  public record XaFunction<T>(Function<Connection, T> function, String jdbcUrl) {}
  public record XaConsumer(Consumer<Connection> consumer, String jdbcUrl) {}

  public static <T> T transaction(Function<Connection, T> function, String jdbcUrl) {
    return transaction(function, jdbcUrl, DEFAULT_ISOLATION_LEVEL);
  }

  public static <T> T transaction(Function<Connection, T> function, String jdbcUrl, int isolationLevel) {
    Connection connection = null;
    try {
      connection = connection(jdbcUrl);
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(isolationLevel);
      T result = function.apply(connection);
      connection.commit();
      connection.setTransactionIsolation(DEFAULT_ISOLATION_LEVEL);
      connection.setAutoCommit(true);
      return result;
    } catch (SQLException e) {
      try {
        if (connection != null) {
          connection.rollback();
          connection.setTransactionIsolation(DEFAULT_ISOLATION_LEVEL);
          connection.setAutoCommit(true);
        }
      } catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
      throw new RuntimeException(e);
    }
  }

  public static <T> T xaTransaction(XaFunction<T>... actions) {
    return xaTransaction(DEFAULT_ISOLATION_LEVEL, actions);
  }

  public static <T> T xaTransaction(int isolationLevel, XaFunction<T>... actions) {
    UserTransaction ut = new UserTransactionImp();
    try {
      ut.begin();
      T result = null;
      for (XaFunction<T> action : actions) {
        Connection connection = connection(action.jdbcUrl);
        connection.setTransactionIsolation(isolationLevel);
        result = action.function.apply(connection);
      }
      ut.commit();
      return result;
    } catch (Exception e) {
      try {
        ut.rollback();
      } catch (SystemException ex) {
        throw new RuntimeException(ex);
      }
      throw new RuntimeException(e);
    }
  }

  public static void transaction(Consumer<Connection> consumer, String jdbcUrl) {
    transaction(consumer, jdbcUrl, TRANSACTION_READ_COMMITTED);
  }

  public static void transaction(Consumer<Connection> consumer, String jdbcUrl, int isolationLevel) {
    Connection connection = null;
    try {
      connection = connection(jdbcUrl);
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(isolationLevel);
      consumer.accept(connection);
      connection.commit();
      connection.setTransactionIsolation(DEFAULT_ISOLATION_LEVEL);
      connection.setAutoCommit(true);
    } catch (SQLException e) {
      try {
        if (connection != null) {
          connection.rollback();
          connection.setTransactionIsolation(DEFAULT_ISOLATION_LEVEL);
          connection.setAutoCommit(true);
        }
      } catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
      throw new RuntimeException(e);
    }
  }

  public static void xaTransaction(XaConsumer... actions) {
    xaTransaction(DEFAULT_ISOLATION_LEVEL, actions);
  }

  public static void xaTransaction(int isolationLevel, XaConsumer... actions) {
    UserTransaction ut = new UserTransactionImp();
    try {
      ut.begin();
      for (XaConsumer action : actions) {
        Connection connection = connection(action.jdbcUrl);
        connection.setTransactionIsolation(isolationLevel);
        action.consumer.accept(connection);
      }
      ut.commit();
    } catch (Exception e) {
      try {
        ut.rollback();
      } catch (SystemException ex) {
        throw new RuntimeException(ex);
      }
      throw new RuntimeException(e);
    }
  }

  private static DataSource dataSource(String jdbcUrl) {
    return datasources.computeIfAbsent(
      jdbcUrl,
      key -> {
        AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
        final String uniqueId = StringUtils.substringAfter(jdbcUrl, "5432/");
        dsBean.setUniqueResourceName(uniqueId);
        dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
        Properties props = new Properties();
        props.setProperty("URL", key);
        props.setProperty("user", "postgres");
        props.setProperty("password", "secret");
        dsBean.setXaProperties(props);
        dsBean.setPoolSize(10);
        return dsBean;
      }
    );
  }

  public static void closeAllConnections() {
    for (Map<String, Connection> connectionMap : treadConnections.values()) {
      for (Connection connection : connectionMap.values()) {
        try {
          if (connection != null && !connection.isClosed()) {
            connection.close();
          }
        } catch (SQLException e) {
          //NOP
        }
      }
    }
  }

  private static Connection connection(String jdbcUrl) throws SQLException {
    return treadConnections.computeIfAbsent(
      Thread.currentThread().threadId(),
      key -> {
        try {
          return new HashMap<>(
            Map.of(
              jdbcUrl,
              dataSource(jdbcUrl).getConnection()
            ));
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    ).computeIfAbsent(
      jdbcUrl,
      key -> {
        try {
          return dataSource(jdbcUrl).getConnection();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    );
  }
}
