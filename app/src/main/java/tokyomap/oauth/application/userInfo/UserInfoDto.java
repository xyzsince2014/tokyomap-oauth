package tokyomap.oauth.application.userInfo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.lang.Nullable;
import tokyomap.oauth.domain.entities.postgres.Usr;

public class UserInfoDto implements Serializable {

  private static final long serialVersionUID = 4417173416400833319L;

  @Nullable
  private String sub;

  @Nullable
  private String familyName;

  @Nullable
  private String givenName;

  @Nullable
  private String middleName;

  @Nullable
  private String nickname;

  @Nullable
  private String preferredUsername;

  @Nullable
  private String profile;

  @Nullable
  private String picture;

  @Nullable
  private String website;

  @Nullable
  private String zoneinfo;

  @Nullable
  private String locale;

  @Nullable
  private String password;

  @Nullable
  private String email;

  @Nullable
  private Boolean emailVerified;

  @Nullable
  private String address;

  @Nullable
  private String phone;

  @Nullable
  private Boolean phoneNumberVerified;

  private String scope;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
  @Nullable
  private LocalDateTime createdAt;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
  @Nullable
  private LocalDateTime updatedAt;

  public UserInfoDto() {}

  public UserInfoDto(Usr usr) {
    this.sub = usr.getSub();
    this.familyName = usr.getFamilyName();
    this.givenName = usr.getGivenName();
    this.middleName = usr.getMiddleName();
    this.nickname = usr.getNickname();
    this.preferredUsername = usr.getPreferredUsername();
    this.profile = usr.getProfile();
    this.picture = usr.getPicture();
    this.website = usr.getWebsite();
    this.zoneinfo = usr.getZoneinfo();
    this.locale = usr.getLocale();
    this.password = usr.getPassword();
    this.email = usr.getEmail();
    this.emailVerified = usr.getEmailVerified();
    this.address = usr.getAddress();
    this.phone = usr.getPhone();
    this.phoneNumberVerified = usr.getPhoneNumberVerified();
    this.scope = usr.getScope();
    this.createdAt = usr.getCreatedAt();
    this.updatedAt = usr.getUpdatedAt();
  }

  public UserInfoDto(Map<String, Object> userInfoMap) {
    this.sub = (String) userInfoMap.get("sub");
    this.familyName = (String) userInfoMap.get("familyName");
    this.givenName = (String) userInfoMap.get("givenName");
    this.middleName = (String) userInfoMap.get("middleName");
    this.nickname = (String) userInfoMap.get("nickname");
    this.preferredUsername = (String) userInfoMap.get("preferredUsername");
    this.profile = (String) userInfoMap.get("profile");
    this.picture = (String) userInfoMap.get("picture");
    this.website = (String) userInfoMap.get("website");
    this.zoneinfo = (String) userInfoMap.get("zoneinfo");
    this.locale = (String) userInfoMap.get("locale");
    this.password = (String) userInfoMap.get("password");
    this.email = (String) userInfoMap.get("email");
    this.emailVerified = (Boolean) userInfoMap.get("emailVerified");
    this.address = (String) userInfoMap.get("address");
    this.phone = (String) userInfoMap.get("phone");
    this.phoneNumberVerified = (Boolean) userInfoMap.get("phoneNumberVerified");
    this.scope = (String) userInfoMap.get("scope");
    this.createdAt = (LocalDateTime) userInfoMap.get("createdAt");
    this.updatedAt = (LocalDateTime) userInfoMap.get("updatedAt");
  }

  @Nullable
  public String getSub() {
    return sub;
  }

  public void setSub(@Nullable String sub) {
    this.sub = sub;
  }

  @Nullable
  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(@Nullable String familyName) {
    this.familyName = familyName;
  }

  @Nullable
  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(@Nullable String givenName) {
    this.givenName = givenName;
  }

  @Nullable
  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(@Nullable String middleName) {
    this.middleName = middleName;
  }

  @Nullable
  public String getNickname() {
    return nickname;
  }

  public void setNickname(@Nullable String nickname) {
    this.nickname = nickname;
  }

  @Nullable
  public String getPreferredUsername() {
    return preferredUsername;
  }

  public void setPreferredUsername(@Nullable String preferredUsername) {
    this.preferredUsername = preferredUsername;
  }

  @Nullable
  public String getProfile() {
    return profile;
  }

  public void setProfile(@Nullable String profile) {
    this.profile = profile;
  }

  @Nullable
  public String getPicture() {
    return picture;
  }

  public void setPicture(@Nullable String picture) {
    this.picture = picture;
  }

  @Nullable
  public String getWebsite() {
    return website;
  }

  public void setWebsite(@Nullable String website) {
    this.website = website;
  }

  @Nullable
  public String getZoneinfo() {
    return zoneinfo;
  }

  public void setZoneinfo(@Nullable String zoneinfo) {
    this.zoneinfo = zoneinfo;
  }

  @Nullable
  public String getLocale() {
    return locale;
  }

  public void setLocale(@Nullable String locale) {
    this.locale = locale;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Nullable
  public String getEmail() {
    return email;
  }

  public void setEmail(@Nullable String email) {
    this.email = email;
  }

  @Nullable
  public Boolean getEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(@Nullable Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  @Nullable
  public String getAddress() {
    return address;
  }

  public void setAddress(@Nullable String address) {
    this.address = address;
  }

  @Nullable
  public String getPhone() {
    return phone;
  }

  public void setPhone(@Nullable String phone) {
    this.phone = phone;
  }

  @Nullable
  public Boolean getPhoneNumberVerified() {
    return phoneNumberVerified;
  }

  public void setPhoneNumberVerified(Boolean phoneNumberVerified) {
    this.phoneNumberVerified = phoneNumberVerified;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
