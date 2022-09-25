package tokyomap.oauth.dtos;

import java.io.Serializable;

public class IntrospectResponseDto extends ApiResponseDto implements Serializable {

  private static final long serialVersionUID = -2461708330251533171L;

  private boolean isActive;

  public IntrospectResponseDto() {}

  public IntrospectResponseDto(boolean isActive) {
    this.isActive = isActive;
  }

  public IntrospectResponseDto(String errorMessage, boolean isActive) {
    super(errorMessage);
    this.isActive = isActive;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}
