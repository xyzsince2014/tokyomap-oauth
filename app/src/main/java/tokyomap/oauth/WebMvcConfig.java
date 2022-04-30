package tokyomap.oauth;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * the application configuration for Spring MVC
 */
@Configuration
@ComponentScan
@EnableWebMvc // register Beans needed by Spring MVC, and enable async communications
@EnableAspectJAutoProxy(proxyTargetClass = true) // enable AOP
@PropertySource("classpath:conf/application.properties")
public class WebMvcConfig implements WebMvcConfigurer {

//  /**
//   * enable injenctions by @Value
//   * unnecessarty from Spring ≥ 4.3
//   * @return PropertySourcesPlaceholderConfigurer
//   */
//  @Bean
//  public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
//    return new PropertySourcesPlaceholderConfigurer();
//  }

  /**
   * enables messages to be read from classpath:/conf/messages.properties via @Autowired messageResource.getMessage(key)
   * @return MessageSource
   */
  @Bean // the default scope of bean managed by the DI container is singleton
  public MessageSource messageSource(@Value("${spring.message_resource.basename}") String basename, @Value("${spring.message_resource.encoding}") String encoding) {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename(basename);
    messageSource.setDefaultEncoding(encoding);
    return messageSource;
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    return new MappingJackson2HttpMessageConverter(Jackson2ObjectMapperBuilder.json().indentOutput(true).build());
  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(0, mappingJackson2HttpMessageConverter());
  }

//  /**
//   * allow CORS for requests
//   * @param registry
//   */
//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/api/**");
//  }

  /**
   * configure async communications
   * @param configurer
   */
  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setDefaultTimeout(5000);
    // configurer.registerCallableInterceptors(new CustomCallableProcessingInterceptor());
    // configurer.registerDeferredResultInterceptors(new CustomDeferredResultProcessingInterceptor());
  }

//  /**
//   * register handler interceptors
//   * @param registry
//   */
//  @Override
//  public void addInterceptors(InterceptorRegistry registry) {
//    registry.addInterceptor(this.loginHandlerInterceptor).addPathPatterns("/login");
//  }
}
