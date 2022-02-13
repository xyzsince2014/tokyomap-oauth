package tokyomap.oauth.dtos;

import java.io.Serializable;

public class RequestApiBaseInfoDto extends RequestBaseInfoDto implements Serializable {

  private static final long serialVersionUID = -5701862915813360709L;

  private String transactionId;

  private String device;

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getDevice() {
    return this.device;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  @Override
  public String toString() {
    return "RequestApiBaseInfoDto [ "
        + super.toString()
        + ", transactionId = " + this.transactionId
        + ", deviceInfo = " + this.device
        + " ]";
  }

}
