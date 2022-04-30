package tokyomap.oauth;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
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
    /** ***********************
     * the root web Application Context
     *
     * Every Spring web application has one associated application context that is tied to its lifecycle: the root web application context.
     * The context is started when the application starts, and it's destroyed when it stops, thanks to a servlet context listener.
     * The most common types of contexts can also be refreshed at runtime, although not all ApplicationContext implementations have this capability.
     *********************** */
    AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();

    // todo: webApplicationContext should have business logics only, e.g. services, repositories, ORMs and data sources
    // register individual @Configuration annotated classes with the context, avoiding package scanning
    webApplicationContext.setConfigLocation(WebMvcConfig.class.getName());
    webApplicationContext.setConfigLocation(JpaConfig.class.getName());
    webApplicationContext.setConfigLocation(WebSecurityConfig.class.getName());
    webApplicationContext.setConfigLocation(AsyncConfig.class.getName());
    webApplicationContext.setConfigLocation(ThymeleafConfig.class.getName());

    // dispose the DI container on JVM shutdown
    webApplicationContext.registerShutdownHook();

    /** ***********************
     * The root web application context is managed by a listener of class org.springframework.web.context.ContextLoaderListener.
     * By default, the listener loads an XML application context from /WEB-INF/webApplicationContext.xml, though the defaults can be changed.
     *********************** */
    // create a ContextLoaderListener with the Application Context above and register it with the ServletContext
    servletContext.addListener(new ContextLoaderListener(webApplicationContext));

    /** ***********************
     * the Application Context for a Dispatcher Servlet
     *
     * A Spring MVC applications can haveDispatcher Servlets.
     * A Dispatcher Servlet receives incoming requests, dispatches them to controller methods, and returns views.
     * Each Dispatcher Servlet has an associated application context.
     *********************** */
    // todo: dispatcherServletContext should have have controllers and views
    AnnotationConfigWebApplicationContext dispatcherServletContext =  new AnnotationConfigWebApplicationContext();
    // todo: dispatcherServletContext.setConfigLocation(DispatcherServletConfig.class.getName());

    /** ***********************
     * instantiate DispatcherServlet (the front controller) and map it to any coming requests
     *
     * For each request, the dispatcherServlet fetches a handler (a method of a controller) with HandlerMapping.getHandler(),
     * and delegates processing the request to it by calling HandlerAdapter.handle().
     *
     * The handler then processes the request to return a view name to the dispatcherServlet, that calls ViewResolver.resolveViewName() to get the View object.
     * Finally, the dispatcherServlet renders the view by View.render(), and returns the response.
     *
     * The dispatcherServlet also calls HttpMessageConverter that converts the request body to a resource class object (DTO) before the handler processes the request.
     * After the handler processes the request to return a resource class object,
     * the dispatcherServlet invokes HttpMessageConverter to write the object into the response body, and returns it to the client.
     *********************** */
    Dynamic dispatcherServlet = servletContext.addServlet("dispatcherServlet", new DispatcherServlet(dispatcherServletContext));
    dispatcherServlet.setLoadOnStartup(1);
    dispatcherServlet.setAsyncSupported(true); // enable dispatcher servlet to do async communications
    dispatcherServlet.addMapping("/");

    servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy()).addMappingForUrlPatterns(null, true,"/*");
    servletContext.addFilter("hiddenHttpMethodFilter", new HiddenHttpMethodFilter()).addMappingForUrlPatterns(null, true, "/*");
  }
}
