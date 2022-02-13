package tokyomap.oauth.common;

import tokyomap.oauth.common.ApiConstants.REDIRECT_FLG;
import tokyomap.oauth.dtos.ApiReturnInfoDto;

/**
 * the common Exception class of this api
 * set error status, status message, and error code to throw an Exception in case an error occurs
 * the Exception thrown is dealt as an Application Error by services
 */
public class ApiException extends Exception {

  private static final long serialVersionUID = 5149898854611171712L;

  /** response info */
  private ApiReturnInfoDto apiReturnInfoDto;

  /**
   * a construtor
   * @param statusCode
   * @param statusMessage
   * @param errorCode
   */
  public ApiException(String statusCode, String statusMessage, String errorCode) {
    super();
    this.apiReturnInfoDto = new ApiReturnInfoDto();
    apiReturnInfoDto.setStatusCode(statusCode);
    apiReturnInfoDto.setStatusMessage(statusMessage);
    apiReturnInfoDto.setErrorCode(errorCode);
    apiReturnInfoDto.setReidrectFlg(REDIRECT_FLG.NOT_REDIRECT.getFlg());
  }

  /**
   * a construtor
   * @param apiReturnInfoDto
   */
  public ApiException(ApiReturnInfoDto apiReturnInfoDto) {
    super();
    this.apiReturnInfoDto = apiReturnInfoDto;
  }

  public ApiReturnInfoDto getApiReturnInfoDto() { return this.apiReturnInfoDto; }

  public void setApiReturnInfoDto(ApiReturnInfoDto apiReturnInfoDto) {this.apiReturnInfoDto = apiReturnInfoDto;}

  public void setStatusCode(String statusCode) {this.apiReturnInfoDto.setStatusCode(statusCode);}

  public void setStatusMessage(String statusMessage) {this.apiReturnInfoDto.setStatusMessage(statusMessage);}

  public void setErrorCode(String errorCode) {this.apiReturnInfoDto.setErrorCode(errorCode);}
}
