package tokyomap.oauth.dtos;

import java.io.Serializable;

public class ResponseBaseInfoDto implements Serializable {

  private static final long serialVersionUID = -5612238613227545601L;

  /** the information object the api returns */
  private ApiReturnInfoDto apiReturnInfo;

  public ResponseBaseInfoDto() {this.apiReturnInfo = new ApiReturnInfoDto();}

  public ApiReturnInfoDto getApiReturnInfo() {
    return apiReturnInfo;
  }

  public void setApiReturnInfo(ApiReturnInfoDto apiReturnInfo) {
    this.apiReturnInfo = apiReturnInfo;
  }

  @Override
  public String toString() {
    return "ResponseBaseInfoDto [ apiReturnInfo = " + this.apiReturnInfo + " ]";
  }
}
