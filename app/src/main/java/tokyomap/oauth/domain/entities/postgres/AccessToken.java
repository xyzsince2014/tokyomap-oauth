package tokyomap.oauth.domain.entities.postgres;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_access_token")
public class AccessToken implements Serializable {

  private static final long serialVersionUID = -71410309563653769L;

  @Id
  @Column(name = "access_token")
  private String accessToken;

  @Column(name = "created_at")
  @Nullable
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @Nullable
  private LocalDateTime updatedAt;

  public AccessToken() {}

  public AccessToken(String accessToken, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.accessToken = accessToken;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  @Nullable
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(@Nullable LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Nullable
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(@Nullable LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
