package tokyomap.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * the application configuration for Spring MVC
 */
@Configuration
@ComponentScan
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource("classpath:/conf/application.properties")
public class ApplicationConfig implements WebMvcConfigurer {
  @Bean
  public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
