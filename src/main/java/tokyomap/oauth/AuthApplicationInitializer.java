package tokyomap.oauth;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

public class AuthApplicationInitializer implements WebApplicationInitializer {

  /**
   * cf. https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/WebApplicationInitializer.html
   * @param servletContainer
   * @throws ServletException
   */
  @Override
  public void onStartup(ServletContext servletContainer) throws ServletException { // container can have Spring MVC's beans, e.g. handlers, handler adaptors, view resolvers
    // create the root Spring application context, which can have Service, Repository, DetaSource, ORM beans
    AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
    webAppContext.setConfigLocation(ApplicationConfig.class.getName());
    webAppContext.setConfigLocation(PersistenceJpaConfig.class.getName());
    webAppContext.setConfigLocation(WebSecurityConfig.class.getName());
    webAppContext.setConfigLocation(ThymeleafConfig.class.getName());

    // register `new ContextLoaderListener(webAppContext)` as a listener of the servletContainer, which enables `webAppContext`
    servletContainer.addListener(new ContextLoaderListener(webAppContext));

    // create the dispatcher servlet's Spring application context
    AnnotationConfigWebApplicationContext dispatcherContext =  new AnnotationConfigWebApplicationContext();
    // dispatcherContext.register(DispatcherConfig.class);

    // register and map `dispatcherServlet`
    ServletRegistration.Dynamic dispatcher = servletContainer.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
  }

}
