package tokyomap.oauth.dtos;

import java.io.Serializable;
import org.springframework.lang.Nullable;

public class ApiResponseDto implements Serializable {

  private static final long serialVersionUID = 6618686241435069855L;

  @Nullable
  private int errorCode;

  @Nullable
  private String errorMessage;

  public ApiResponseDto() {}

  public ApiResponseDto(int errorCode, String errorMessage) {
    this.errorCode =errorCode;
    this.errorMessage = errorMessage;
  }

  @Nullable
  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(@Nullable int errorCode) {
    this.errorCode = errorCode;
  }

  @Nullable
  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(@Nullable String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
