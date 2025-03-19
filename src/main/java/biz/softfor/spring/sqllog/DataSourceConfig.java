package biz.softfor.spring.sqllog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.datasource")
@Getter
@Setter
@NoArgsConstructor
public class DataSourceConfig {

  private String host;
  private String port;
  private String name;
  private String url;
  private String username;
  private String password;
  private String driverClassName;

}
