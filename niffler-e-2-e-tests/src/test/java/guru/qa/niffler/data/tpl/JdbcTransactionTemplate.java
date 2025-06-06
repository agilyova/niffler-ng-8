package guru.qa.niffler.data.tpl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

@ParametersAreNonnullByDefault
public class JdbcTransactionTemplate {

  private final JdbcConnectionHolder holder;
  private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

  public JdbcTransactionTemplate(String jdbcUrl) {
    this.holder = Connections.holder(jdbcUrl);
  }

  public JdbcTransactionTemplate holdConnectionAfterAction() {
    this.closeAfterAction.set(false);
    return this;
  }

  public @Nullable <T> T execute(Supplier<T> action) {
    return execute(action, TRANSACTION_READ_COMMITTED);
  }

  public @Nullable <T> T execute(Supplier<T> action, int isolationLevel) {
    Connection connection = null;
    try {
      connection = holder.connection();
      connection.setAutoCommit(false);
      connection.setTransactionIsolation(isolationLevel);
      T result = action.get();
      connection.commit();
      connection.setTransactionIsolation(TRANSACTION_READ_COMMITTED);
      connection.setAutoCommit(true);
      return result;
    } catch (Exception e) {
      try {
        if (connection != null) {
          connection.rollback();
          connection.setTransactionIsolation(TRANSACTION_READ_COMMITTED);
          connection.setAutoCommit(true);
        }
      } catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
      throw new RuntimeException(e);
    } finally {
      if (closeAfterAction.get()) {
        holder.close();
      }
    }
  }
}
