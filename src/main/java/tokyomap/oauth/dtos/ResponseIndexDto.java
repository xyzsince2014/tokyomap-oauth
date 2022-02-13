package tokyomap.oauth.dtos;

import java.io.Serializable;

public class ResponseIndexDto extends ResponseBaseInfoDto implements Serializable {

  private static final long serialVersionUID = -3061789107785184885L;

  /** the request information */
  private RequestIndexDto dataInfo;

  public RequestIndexDto getDataInfo() {
    return dataInfo;
  }

  public void setDataInfo(RequestIndexDto dataInfo) {
    this.dataInfo = dataInfo;
  }

  @Override
  public String toString() {
    return "ResponseIndexDto [ dataInfo = " + this.dataInfo + " ]";
  }
}
