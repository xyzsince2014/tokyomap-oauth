package tokyomap.oauth.application.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.lang.Nullable;

public class UserDto implements Serializable {

  private static final long serialVersionUID = 5045870685362456022L;

  private String userId;
  private String givenName;
  private String familyName;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Nullable
  private LocalDate createdAt;

  public UserDto() {}

  public UserDto(String userId, String givenName, String familyName) {
    this.userId = userId;
    this.givenName = givenName;
    this.familyName = familyName;
    this.createdAt = null; // todo
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }
}
