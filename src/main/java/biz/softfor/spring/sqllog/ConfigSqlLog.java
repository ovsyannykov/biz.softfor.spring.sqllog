package biz.softfor.spring.sqllog;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.p6spy.engine.spy.P6DataSource;
import java.util.List;
import javax.sql.DataSource;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan
@PropertySource("classpath:/biz.softfor.spring.sqllog.properties")
@EnableConfigurationProperties(DataSourceConfig.class)
public class ConfigSqlLog {

  public static void log(java.util.logging.Logger log) {
    log.info(() -> {
      QueryCount c = QueryCountHolder.getGrandTotal();
      return "Queries count:"
      + "\nINSERT=" + c.getInsert()
      + "\nSELECT=" + c.getSelect()
      + "\nUPDATE=" + c.getUpdate()
      + "\nDELETE=" + c.getDelete()
      + "\nTOTAL=" + c.getTotal();
    });

  }

  private final static String BATCH_LOGGING_LEVEL_KEY
  = "logging.level." + SLF4JQueryLoggingListener.class.getPackageName();

  @Autowired
  private Environment env;

  @org.springframework.context.annotation.Bean
  public DataSource dataSource(DataSourceConfig cfg) {
    if(env.getProperty(BATCH_LOGGING_LEVEL_KEY) == null) {
      LoggerContext ctx = (LoggerContext)LoggerFactory.getILoggerFactory();
      ctx.getLogger(SLF4JQueryLoggingListener.class).setLevel(Level.DEBUG);
    }
    DataSource dataSource = DataSourceBuilder.create()
    .driverClassName(cfg.getDriverClassName()).url(cfg.getUrl())
    .username(cfg.getUsername()).password(cfg.getPassword()).build();
    dataSource = new P6DataSource(dataSource);
    dataSource = ProxyDataSourceBuilder.create("ProxyDataSource", dataSource)
    .countQuery().listener(new LoggingListener()).multiline().build();
    return dataSource;
  }

  private static class LoggingListener extends SLF4JQueryLoggingListener {

    LoggingListener() {
      setQueryLogEntryCreator(new DefaultQueryLogEntryCreator() {

        @Override
        protected String formatQuery(String query) {
          return FormatStyle.BASIC.getFormatter().format(query);
        }

      });
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
      if(execInfo.isBatch()) {
        super.afterQuery(execInfo, queryInfoList);
      }
    }

  }

}
