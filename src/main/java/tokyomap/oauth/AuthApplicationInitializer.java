package tokyomap.oauth;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import tokyomap.oauth.web.ApplicationConfig;
import tokyomap.oauth.web.PersistenceJpaConfig;
import tokyomap.oauth.web.ThymeleafConfig;
import tokyomap.oauth.web.WebSecurityConfig;

public class AuthApplicationInitializer implements WebApplicationInitializer {

  /**
   * @see @link{https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/WebApplicationInitializer.html}
   * @see @link{https://www.baeldung.com/spring-web-contexts}
   * @param servletContext
   * @throws ServletException
   */
  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    /** ***********************
     * the Spring Application Context (the DI container)
     *
     * Every Spring webapp has an associated application context that is tied to its lifecycle: the root web application context.
     * The context is started when the application starts, and it's destroyed when it stops, thanks to a servlet context listener.
     * The most common types of contexts can also be refreshed at runtime, although not all ApplicationContext implementations have this capability.
     *********************** */
    AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();

    // register individual @Configuration annotated classes with the context, avoiding package scanning
    webAppContext.setConfigLocation(ApplicationConfig.class.getName());
    webAppContext.setConfigLocation(PersistenceJpaConfig.class.getName());
    webAppContext.setConfigLocation(WebSecurityConfig.class.getName());
    webAppContext.setConfigLocation(ThymeleafConfig.class.getName());

    /** ***********************
     * The root web application context is managed by a listener of class org.springframework.web.context.ContextLoaderListener.
     * By default, the listener loads an XML application context from /WEB-INF/applicationContext.xml.
     * Those defaults can be changed.
     *********************** */
    // create a ContextLoaderListener with the Application Context above and register it with the ServletContext
    servletContext.addListener(new ContextLoaderListener(webAppContext));

    /** ***********************
     * the Spring Application Context for the Dispatcher Servlet
     *
     * Spring MVC applications have at least one Dispatcher Servlet configured (but possibly more than one).
     * This is the servlet that receives incoming requests, dispatches them to the appropriate controller method, and returns the view.
     * Each DispatcherServlet has an associated application context.
     *********************** */
    AnnotationConfigWebApplicationContext dispatcherContext =  new AnnotationConfigWebApplicationContext();
    // todo: dispatcherContext.register(DispatcherServletConfig.class);

    // instantiate DispatcherServlet and map it for requests for "/web/*"
    Dynamic webServlet = servletContext.addServlet("webServlet", new DispatcherServlet(dispatcherContext));
    webServlet.setLoadOnStartup(1);
    webServlet.addMapping("/web");

    // todo: do in a Configuration class
//    AnnotationConfigWebApplicationContext apiAppContext = new AnnotationConfigWebApplicationContext();
//    apiAppContext.setConfigLocation(JerseyConfig.class.getName());
//    Dynamic apiServlet = servletContext.addServlet("apiServlet", new DispatcherServlet(apiAppContext));
//    apiServlet.setLoadOnStartup(1);
//    apiServlet.addMapping("/api");
//    Dynamic apiServlet = servletContext.addServlet("apiServlet", new ServletContainer(resourceConfig));
//    apiServlet.setLoadOnStartup(2);
//    apiServlet.addMapping("/api");

    /* the S2Container */
    SingletonS2ContainerFactory.setConfigPath("application.dicon");
    SingletonS2ContainerFactory.init();
  }
}
