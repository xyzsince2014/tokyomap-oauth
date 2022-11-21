package tokyomap.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60 * 60) // replaces HttpSession with SpringSession
@PropertySource("classpath:conf/session.properties")
public class SessionConfig {

  @Value("${session.redis.host}") private String host;
  @Value("${session.redis.port}") private String port;

  @Bean
  public static ConfigureRedisAction configureRedisAction() {
    return ConfigureRedisAction.NO_OP;
  }

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory factory = new JedisConnectionFactory();
    factory.setHostName(this.host);
    factory.setPort(Integer.parseInt(this.port));
    factory.setUsePool(true);
    return factory;
  }

  /**
   * manage session id in cookie
   * @return
   */
  @Bean
  public CookieHttpSessionStrategy httpSessionStrategy() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setUseSecureCookie(true);
    serializer.setUseHttpOnlyCookie(true);
    serializer.setCookieMaxAge(60 * 60);
    serializer.setCookieName("JSESSIONID");

    CookieHttpSessionStrategy strategy = new CookieHttpSessionStrategy();
    strategy.setCookieSerializer(serializer);
    return strategy;
  }
}
