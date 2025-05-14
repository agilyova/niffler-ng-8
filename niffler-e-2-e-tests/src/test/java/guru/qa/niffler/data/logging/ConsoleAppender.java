package guru.qa.niffler.data.logging;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.StdoutLogger;

public class ConsoleAppender extends StdoutLogger {
  @Override
  public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
    System.out.println("### SQL: " + sql);
  }
}
