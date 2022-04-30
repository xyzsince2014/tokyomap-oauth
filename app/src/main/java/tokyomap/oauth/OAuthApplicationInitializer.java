package tokyomap.oauth;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class OAuthApplicationInitializer implements WebApplicationInitializer {

  /**
   * @see @link{https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/WebApplicationInitializer.html}
   * @see @link{https://www.baeldung.com/spring-web-contexts}
   * @param servletContext
   * @throws ServletException
   */
  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {

    AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
    webApplicationContext.setConfigLocation(WebMvcConfig.class.getName());
    webApplicationContext.setConfigLocation(JpaConfig.class.getName());
    webApplicationContext.registerShutdownHook(); // dispose the DI container on JVM shutdown
    servletContext.addListener(new ContextLoaderListener(webApplicationContext));

    AnnotationConfigWebApplicationContext dispatcherContext =  new AnnotationConfigWebApplicationContext();
    Dynamic webServlet = servletContext.addServlet("dispatcherServlet", new DispatcherServlet(dispatcherContext));
    webServlet.setLoadOnStartup(1);
    webServlet.addMapping("/");

    // add the filter to convert posted method parameters into HTTP methods
    servletContext.addFilter("hiddenHttpMethodFilter", new HiddenHttpMethodFilter()).addMappingForUrlPatterns(null, true, "/*");
  }
}
