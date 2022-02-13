package tokyomap.oauth.dtos;

import java.io.Serializable;

public class RequestIndexDto extends RequestApiBaseInfoDto implements Serializable {

  private static final long serialVersionUID = -8624164036587857823L;

  private String message;

  private String status;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "RequestIndexDto [ "
        + super.toString()
        + ", message = " + this.message
        + ", status = " + this.status
        + " ]";
  }
}
