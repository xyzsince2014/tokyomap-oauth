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

    // todo: webApplicationContext should have business logics only, e.g. services, repositories, ORMs and data sources
    AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
    webApplicationContext.setConfigLocations(JpaConfig.class.getName());
    webApplicationContext.setConfigLocation(WebMvcConfig.class.getName());
    webApplicationContext.registerShutdownHook();
    servletContext.addListener(new ContextLoaderListener(webApplicationContext));

    // todo: dispatcherServletContext should have have controllers and views
    AnnotationConfigWebApplicationContext dispatcherServletContext =  new AnnotationConfigWebApplicationContext();
    // todo: dispatcherServletContext.setConfigLocation(DispatcherServletConfig.class.getName());
    Dynamic dispatcherServlet = servletContext.addServlet("dispatcherServlet", new DispatcherServlet(dispatcherServletContext));
    dispatcherServlet.setLoadOnStartup(1);
    // todo: dispatcherServlet.setAsyncSupported(true);
    dispatcherServlet.addMapping("/");

    servletContext.addFilter("hiddenHttpMethodFilter", new HiddenHttpMethodFilter()).addMappingForUrlPatterns(null, true, "/*");
  }
}
