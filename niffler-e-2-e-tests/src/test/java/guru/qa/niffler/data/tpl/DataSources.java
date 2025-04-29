package guru.qa.niffler.data.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.commons.lang3.StringUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSources {
  private static final Map<String, DataSource> datasources = new ConcurrentHashMap<>();

  private DataSources() {
  }

  public static DataSource dataSource(String jdbcUrl) {
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
        dsBean.setPoolSize(3);
        dsBean.setMaxPoolSize(10);
        try {
          InitialContext context = new InitialContext();
          context.bind("java:comp/env/jdbc/" + uniqueId, dsBean);
        } catch (NamingException e) {
          throw new RuntimeException(e);
        }
        return dsBean;
      }
    );
  }
}
