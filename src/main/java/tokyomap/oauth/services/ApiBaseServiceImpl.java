package tokyomap.oauth.services;

import javax.annotation.Resource;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.util.StringUtil;
import tokyomap.oauth.dtos.RequestApiBaseInfoDto;
import tokyomap.oauth.dtos.RequestBaseInfoDto;
import tokyomap.oauth.dtos.ResponseBaseInfoDto;
import tokyomap.oauth.utils.ApiRequest;
import tokyomap.oauth.utils.ApiRequest.PApiRequest;

public abstract class ApiBaseServiceImpl extends BaseServiceImpl {
  @Resource
  S2Container container;

  /** multiple pushes error */
  private static final String APP_ERR_1002 = "APP_ERR.1002";

  /**
   * do the pre execution process
   * @param requestDto request info
   * @return request info
   * @throws Exception in case an error occurs in services or logics
   */
  @Override
  protected RequestBaseInfoDto doPreExecution(RequestBaseInfoDto requestDto) throws Exception {
    // set request info
    setApiRequest(requestDto);

    if (StringUtil.isNotEmpty(((RequestApiBaseInfoDto) requestDto).getTransactionId())) {
//      String errorMsg = this.orderLogic.checkRegist((RequestApiBaseInfoDto) requestDto);
//      if (StringUtil.isNotEmpty(errorMsg)) {
//        Integer paymentId = ((RequestApiBaseInfoDto) requestDto).getPaymentId();
//
//        throw new ApiException(
//            ApiConstants.APPLICATION_ERROR,
//            ApiProperties.getMessage(ApiConstants.APPLICATION_ERROR, errorMsg),
//            ApiProperties.getProperty(PROPERTIES_TYPE.ERR_CODE, APP_ERR_1002)
//        );
//      }
    }

    return requestDto;
  }

  /**
   * do the post execution process
   * @param responseDto response info
   * @return response info
   * @throws Exception in case an error occurs in services or logics
   */
  @Override
  protected ResponseBaseInfoDto doPostExecution(ResponseBaseInfoDto responseDto) throws Exception {
    // do nothing
    return responseDto;
  }

  /**
   * set request info
   * @param requestDto request info
   */
  private void setApiRequest(RequestBaseInfoDto requestDto) {
    RequestApiBaseInfoDto requestBaseInfoDto = (RequestApiBaseInfoDto) requestDto;

    ApiRequest apiRequest = (ApiRequest) this.container.getComponent(ApiRequest.class);
    PApiRequest pApiRequest = apiRequest.new PApiRequest();
//    pApiRequest.setBookstoreGroupType(requestBaseInfoDto.getBookstoreGroupType());
    apiRequest.setRequest(pApiRequest);
   }
}
