package tokyomap.oauth.services;

import java.util.List;
import javax.annotation.Resource;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tokyomap.oauth.common.ApiConstants;
import tokyomap.oauth.common.ApiException;
import tokyomap.oauth.common.ApiProperties;
import tokyomap.oauth.common.ApiProperties.PROPERTIES_TYPE;
import tokyomap.oauth.dtos.ApiReturnInfoDto;
import tokyomap.oauth.dtos.RequestBaseInfoDto;
import tokyomap.oauth.dtos.ResponseBaseInfoDto;
import tokyomap.oauth.utils.Checker;
import tokyomap.oauth.utils.CheckerListFactory;
import tokyomap.oauth.utils.Validator;

public abstract class BaseServiceImpl {
  private static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

  /** application error code */
  private static final String APP_ERR_1000 = "APP_ERR.1000";

  /** system error code */
  private static final String SYS_ERR_1000 = "SYS_ERR.1000";

  /** validation error code */
  private static final String APP_ERR_1001 = "APP_ERR.1001";

  /** context path */
  private String contextpath;

  @Resource
  private UserTransaction userTransaction;

  protected String getContextpath() {
    return contextpath;
  }

  protected void setContextpath(String contextpath) {
    this.contextpath = contextpath;
  }

  /**
   * get the list of Checker for validation
   * @param requestDto request info
   * @return list of Chcker
   */
  protected List<Checker> getCheckerList(RequestBaseInfoDto requestDto) {
    return CheckerListFactory.makeCheckerList(requestDto);
  }

  /**
   * do pre-process before execution
   * @param requestDto request info
   * @return request info
   * @throws Exception error in service or logic layer
   */
  protected abstract RequestBaseInfoDto doPreExecution(RequestBaseInfoDto requestDto) throws Exception;

  /**
   * do execution
   * @param requestDto request info
   * @return response info
   * @throws Exception error in service or logic layer
   */
  protected abstract ResponseBaseInfoDto doExecution(RequestBaseInfoDto requestDto) throws Exception;

  /**
   * do post-process after execution
   * @param responseDto response info
   * @return response info
   * @throws Exception error in service or logic layer
   */
  protected abstract ResponseBaseInfoDto doPostExecution(ResponseBaseInfoDto responseDto) throws Exception;

  /**
   * make a ResposneDto object
   * @param apiReturnInfoDto API response status info
   * @param requestDto request info
   * @return response info
   */
  protected abstract ResponseBaseInfoDto getResponseDto(ApiReturnInfoDto apiReturnInfoDto, RequestBaseInfoDto requestDto);

  /**
   * the base method of the service layer
   * @param requestDto request info
   * @param ctxPath context path
   * @return response info
   */
  public ResponseBaseInfoDto execute(RequestBaseInfoDto requestDto, String ctxPath) {
    this.contextpath = ctxPath;

    ResponseBaseInfoDto responseDto = null;

    try {
      // validation
      Validator validator = new Validator();
      if (!validator.execute(getCheckerList(requestDto), requestDto)) {
        ApiException e = new ApiException(
            ApiConstants.APPLICATION_ERROR,
            ApiProperties.getMessage(ApiConstants.APPLICATION_ERROR, validator.getMessage()),
            ApiProperties.getProperty(PROPERTIES_TYPE.ERR_CODE, APP_ERR_1001)
        );
        throw e;
      }

      // pre-execution
      requestDto = doPreExecution(requestDto);

      // execution
      responseDto = doExecution(requestDto);

      // post-execution
      responseDto = doPostExecution(responseDto);

    } catch (ApiException e) {
      // Application Error if ApiException is thrown
      // set the default error code if the error code is not set
      if (e.getApiReturnInfoDto().getErrorCode() == null || e.getApiReturnInfoDto().getErrorCode().isEmpty()) {
        ApiProperties.getProperty(PROPERTIES_TYPE.ERR_CODE, APP_ERR_1000);
      }
      responseDto = getResponseDto(e.getApiReturnInfoDto(), requestDto);

      // rollback
      responseDto = rollback(requestDto, responseDto);

    } catch (Exception e) {
      // System Error if Exception is thrown
      responseDto = getResponseDto(makeSystemErrorInfo(e), requestDto);

      // rollback
      responseDto = rollback(requestDto, responseDto);

      logger.error("System Error - " + e);
    }

    return responseDto;
  }

  /**
   * make ApiReturnInfo object for System Error
   * @param e Throwable
   * @return ApiReturnInfo
   */
  private ApiReturnInfoDto makeSystemErrorInfo(Throwable e) {
    ApiReturnInfoDto apiReturnInfoDto = new ApiReturnInfoDto();
    apiReturnInfoDto.setStatusCode(ApiConstants.SYSTEM_ERROR);
    apiReturnInfoDto.setStatusMessage(ApiProperties.getMessage(ApiConstants.SYSTEM_ERROR, e.getMessage()));
    apiReturnInfoDto.setErrorCode(ApiProperties.getProperty(PROPERTIES_TYPE.ERR_CODE, SYS_ERR_1000));
    return apiReturnInfoDto;
  }

  /**
   * rollback the transaction
   * @param requestDto request info
   * @param responseDto response info
   * @return response info
   */
  private ResponseBaseInfoDto rollback(RequestBaseInfoDto requestDto, ResponseBaseInfoDto responseDto) {
    try {
      this.userTransaction.setRollbackOnly();
    } catch (Exception e) {
      logger.error("failed to rollback the transaction ", e);
      responseDto = getResponseDto(makeSystemErrorInfo(e), requestDto);
    }
    return responseDto;
  }

}
