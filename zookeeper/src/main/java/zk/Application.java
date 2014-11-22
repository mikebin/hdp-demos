package zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

  private static final String NAMESPACE = "appconfig";
  private static final String APP_NAME = "myapp";

  @Value("${env}")
  private String env;

  @Autowired
  private CuratorFramework zk;

  @Bean
  public CuratorFramework zkClient(@Value("${zk.connection.string:localhost:2181}") String connectionString,
                                   @Value("${zk.connection.timeout.ms:10000}") int connectionTimeoutMs,
                                   @Value("${zk.session.timeout.ms:60000}") int sessionTimeoutMs,
                                   @Value("${zk.retry.interval:5000}") int retryInterval,
                                   @Value("${zk.retry.attempts:3}") int retryAttemptsCount) {
    CuratorFramework zk = CuratorFrameworkFactory.builder()
        .connectString(connectionString)
        .connectionTimeoutMs(connectionTimeoutMs)
        .sessionTimeoutMs(sessionTimeoutMs)
        .retryPolicy(retryPolicy(retryInterval, retryAttemptsCount)).namespace(NAMESPACE).build();
    zk.start();
    return zk;
  }

  private RetryPolicy retryPolicy(int retryInterval, int retryAttemptsCount) {
    return new ExponentialBackoffRetry(retryInterval, retryAttemptsCount);
  }

  @RequestMapping("/config")
  @ResponseBody
  AppConfig config() throws Exception {
    AppConfig config = new AppConfig();
    config.setDbUrl(getStringDataForPath("/db/url"));
    config.setDbUser(getStringDataForPath("/db/user"));
    config.setDbPassword(getStringDataForPath("/db/password"));
    return config;
  }

  @RequestMapping(value = "/env", produces = MediaType.TEXT_PLAIN_VALUE)
  String env() throws Exception {
    return env;
  }

  private String getStringDataForPath(String path) throws Exception {
    return new String(zk.getData().forPath("/" + env + "/" + APP_NAME + path));
  }

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Application.class, args);
  }
}


