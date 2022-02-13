package tokyomap.oauth.controllers.api;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.seasar.framework.container.ExternalContext;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.external.servlet.HttpServletExternalContext;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tokyomap.oauth.common.ApiConstants;
import tokyomap.oauth.dtos.ApiReturnInfoDto;
import tokyomap.oauth.dtos.RequestApiBaseInfoDto;
import tokyomap.oauth.dtos.RequestIndexDto;
import tokyomap.oauth.dtos.ResponseIndexDto;
import tokyomap.oauth.services.IndexService;

@Component
@Path("/index")
public class IndexController {
  private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

  private static final int HTTP_PORT = 80;

  private static final int HTTPS_PORT = 443;

  @Context
  private ServletConfig config;

  @Context
  private ServletContext context;

  @Context
  private HttpServletRequest request;

  @Context
  private HttpServletResponse response;

  private final S2Container container;

  public static final String ATTRIBUTE_REQUEST_DTO = "IndexController_RequestDto";

  /**
   * the constructor
   * cf. https://s2container.seasar.org/2.4/ja/DIContainer.html#S2ContainerDefinition
   */
  public IndexController() {
    this.container = SingletonS2ContainerFactory.getContainer();
    this.container.setExternalContext(new HttpServletExternalContext());
    this.container.init();
  }

  @GET
  @Path("/uriinfo")
  public String uriinfo(@Context UriInfo uriInfo) {
    return uriInfo.getPath() + ", " + uriInfo.getQueryParameters().getFirst("foo");
  }

  @GET
  @Path("/headers")
  public String headers(@Context HttpHeaders headers) {
    return headers.getHeaderString("Referer:");
  }

  @GET
  @Path("/{message}")
  @Produces({MediaType.APPLICATION_JSON})
  public String getMessage(@PathParam("message") String message, @QueryParam("data") String data) {
    return message + ", " + data;
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public ResponseIndexDto index(RequestIndexDto requestDto) {
    logger.info("perform /api/index");

    setExternalContext(requestDto);

    // create the context URL
    String contextPath = createContextPath(this.request.getScheme(), this.request.getServerName(), this.request.getServerPort(), this.context.getContextPath());

    IndexService indexService = (IndexService) this.container.getComponent(IndexService.class);
    ResponseIndexDto responseDto = (ResponseIndexDto) indexService.execute(requestDto, contextPath);

    setHttpStatus(responseDto.getApiReturnInfo());

    return responseDto;
  }

  /**
   * set the HTTP status according to the status code of the api response
   * @param apiReturnInfoDto ApiResponseInfoDto
   */
  private void setHttpStatus(ApiReturnInfoDto apiReturnInfoDto) {
    this.response.setStatus(getHttpStatus(apiReturnInfoDto.getStatusCode()));
  }

  /**
   * get the Http status code according to the status code of the api response
   * @param statusCode the status code of the api response
   * @return the Http status code
   */
  private int getHttpStatus(String statusCode) {
    int httpStatus;
    switch (statusCode) {
      case ApiConstants.SYSTEM_ERROR:
        httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        break;
      default:
        httpStatus = HttpServletResponse.SC_OK;
        break;
    }
    return httpStatus;
  }

  /**
   * create the context URL
   * @param scheme
   * @param server
   * @param port
   * @param contextRoot
   * @return the context URL
   */
  private static String createContextPath(String scheme, String server, int port, String contextRoot) {
    StringBuffer url = new StringBuffer();
    if (port < 0) {
      port = HTTP_PORT; // default port
    }

    url.append(scheme).append("://").append(server);

    if ((scheme.equals("http") && port != HTTP_PORT) || (scheme.equals("https") && port != HTTPS_PORT)) {
      url.append(":").append(port);
    }

    url.append(contextRoot).append("/api");

    return url.toString();
  }

  /**
   * set the external context to use the Spring Web context in S2Container
   * @param requestDto RequestApiBaseInfoDto
   */
  private void setExternalContext(RequestApiBaseInfoDto requestDto) {
    if (this.context != null) {
      this.request.setAttribute(ATTRIBUTE_REQUEST_DTO, requestDto);
      setExternalContext();
    }
  }

  /**
   * set the external context to use the Spring Web context in S2Container
   * fetch the HttpServletExternalContext, that is set on S2Container initialisation, by S2Container#getExternalContext(), and then do the processed below
   * * ExternalContext#setApplication(Object)
   * * ExternalContext#setRequest(Object)
   * * ExternalContext#setResponse(Object)
   */
  private void setExternalContext() {
    ExternalContext externalContext = this.container.getExternalContext();
    if (this.context != null) {
      externalContext.setApplication(this.context);
      externalContext.setRequest(this.request);
      externalContext.setResponse(this.response);
    }
  }
}
