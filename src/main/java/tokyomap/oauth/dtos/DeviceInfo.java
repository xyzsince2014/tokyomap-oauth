package tokyomap.oauth.dtos;

import java.io.Serializable;

public class DeviceInfo implements Serializable {

  private static final long serialVersionUID = 2187090779285257537L;

  private String osName;

  private String deviceId;

  private String deviceName;

  /** 機種SEQ */
  private Long modelSeq;

  /** 端末名 */
  private String terminalName;

  public String getOsName() {
    return osName;
  }

  public void setOsName(String osName) {
    this.osName = osName;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public void setDeviceName(String deviceName) {
    this.deviceName = deviceName;
  }

  public Long getModelSeq() {
    return modelSeq;
  }

  public void setModelSeq(Long modelSeq) {
    this.modelSeq = modelSeq;
  }

  public String getTerminalName() {
    return terminalName;
  }

  public void setTerminalName(String terminalName) {
    this.terminalName = terminalName;
  }

  @Override
  public String toString() {
    return "DeviceInfoDto [ "
        + "osName = " + this.osName
        + ", deviceId = " + this.deviceId
        + ", deviceName = " + this.deviceName
        + ", modelSeq = " + this.modelSeq
        + ", terminalName = " + this.terminalName
        + " ]";
  }
}
