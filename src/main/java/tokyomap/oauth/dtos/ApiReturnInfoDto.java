package tokyomap.oauth.dtos;

import java.io.Serializable;
import tokyomap.oauth.common.ApiConstants;

public class ApiReturnInfoDto implements Serializable {

  private static final long serialVersionUID = -2569471279682736438L;

  private String statusCode;

  private String statusMessage;

  private String errorCode;

  private Integer reidrectFlg;

  private String dataInfo;

  public ApiReturnInfoDto() {
    this.statusCode = "200";
    this.errorCode = "0";
    this.statusMessage = null;
    setReidrectFlg(ApiConstants.REDIRECT_FLG.NOT_REDIRECT.getFlg());
  }

  public ApiReturnInfoDto(String statusMessage) {
    this.statusCode = "200";
    this.errorCode = "0";
    this.statusMessage = statusMessage;
  }

  public ApiReturnInfoDto(String statusMessage, String dataInfo) {
    this.statusCode = "200";
    this.errorCode = "0";
    this.statusMessage = statusMessage;
    this.dataInfo = dataInfo;
  }

  public String getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public Integer getReidrectFlg() {
    return reidrectFlg;
  }

  public void setReidrectFlg(Integer reidrectFlg) {
    this.reidrectFlg = reidrectFlg;
  }

  public String getDataInfo() {
    return dataInfo;
  }

  public void setDataInfo(String dataInfo) {
    this.dataInfo = dataInfo;
  }

  @Override
  public String toString() {
    return "ApiReturnInfoDto [ "
        + ", statusCode  = " + this.statusCode
        + ", statusMessage = " + this.statusMessage
        + ", errorCode = " + this.errorCode
        + "]";
  }
}
