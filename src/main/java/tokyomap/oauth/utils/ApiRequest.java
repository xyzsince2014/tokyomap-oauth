package tokyomap.oauth.utils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.util.StringUtil;
import org.springframework.stereotype.Component;
import tokyomap.oauth.common.ApiConfigs;
import tokyomap.oauth.common.ApiConfigs.CONFIG_TYPE;

/**
 * the class to manipulate the common request info of this api
 */
@Component
public final class ApiRequest {
  @Resource
  S2Container container;

  /**
   * get the request info
   * @return request info
   */
  public PApiRequest getReqeust() {
    PApiRequest apiRequest = null;
    HttpServletRequest request = getHttpServletRequest();
    if(request != null) {
      apiRequest = (PApiRequest) request.getAttribute(PApiRequest.class.getCanonicalName());
    }
    return apiRequest;
  }

  /**
   * set the request info
   * @param apiRequest request info
   */
  public void setRequest(PApiRequest apiRequest) {
    HttpServletRequest request = getHttpServletRequest();
    if(request != null) {
      request.setAttribute(PApiRequest.class.getCanonicalName(), apiRequest);
    }
  }

  /**
   * get a HttpServletRequest object
   * @return HttpServletRequest object
   */
  private HttpServletRequest getHttpServletRequest() {
    HttpServletRequest request = null;
    if(this.container.getExternalContext() != null) {
      request = (HttpServletRequest) this.container.getExternalContext().getRequest();
    }
    return request;
  }

  /**
   * return hoge,
   * return default STATUS.HOGE if it were not for the hoge or HTTP request
   * @return hoge
   */
  public String getHoge() {
    PApiRequest pApiRequest = getReqeust();
    String hoge = ApiConfigs.get(CONFIG_TYPE.HOGE, "STATUS.HOGE");
    if (pApiRequest != null && !StringUtil.isBlank(pApiRequest.getHoge())) {
      hoge = pApiRequest.getHoge();
    }
    return hoge;
  }

  /**
   * the request info class common in this api
   */
  public final class PApiRequest {
    private String hoge;

    public String getHoge() {
      return hoge;
    }

    public void setHoge(String hoge) {
      this.hoge = hoge;
    }
  }
}
