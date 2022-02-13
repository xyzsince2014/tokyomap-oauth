package tokyomap.oauth.dtos;

import java.io.Serializable;

public class RequestBaseInfoDto implements Serializable {

  private static final long serialVersionUID = -3012775939086346847L;

  private Integer additionalHour;

  public Integer getAdditionalHour() {
    return additionalHour;
  }

  public void setAdditionalHour(Integer additionalHour) {
    this.additionalHour = additionalHour;
  }

  @Override
  public String toString() {
    return "RequestBaseInfoDto [ "
        + ", additionalHour = " + this.additionalHour
        + " ]";
  }
}
