package tokyomap.oauth.domain.entities.postgres;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_usr")
public class Usr implements Serializable {

  private static final long serialVersionUID = 6550687434599143082L;

  @Id
  @Column(name = "sub")
  private String sub;

  @Column(name = "name")
  private String name;

  @Column(name = "family_name")
  private String familyName;

  @Column(name = "given_name")
  private String givenName;

  @Column(name = "middle_name")
  private String middleName;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "preferred_username")
  private String preferredUsername;

  @Column(name = "profile")
  private String profile;

  @Column(name = "picture")
  private String picture;

  @Column(name = "website")
  private String website;

  @Column(name = "zoneinfo")
  private String zoneinfo;

  @Column(name = "locale")
  private String locale;

  @Column(name = "password")
  private String password;

  @Column(name = "email")
  private String email;

  @Column(name = "email_verified")
  private Boolean emailVerified;

  @Column(name = "address")
  private String address;

  @Column(name = "phone")
  private String phone;

  @Column(name = "phone_number_verified")
  private Boolean phoneNumberVerified;

  @Column(name = "scope")
  private String scope;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Usr() {}

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getPreferredUsername() {
    return preferredUsername;
  }

  public void setPreferredUsername(String preferredUsername) {
    this.preferredUsername = preferredUsername;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getZoneinfo() {
    return zoneinfo;
  }

  public void setZoneinfo(String zoneinfo) {
    this.zoneinfo = zoneinfo;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

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

  public Map<String, Object> convertToMap() {
    Map<String, Object> usrMap = new HashMap();
    usrMap.put("sub", this.sub);
    usrMap.put("name", this.name);
    usrMap.put("familyName", this.familyName);
    usrMap.put("givenName", this.givenName);
    usrMap.put("middleName", this.middleName);
    usrMap.put("nickname", this.nickname);
    usrMap.put("preferredUsername", this.preferredUsername);
    usrMap.put("profile", this.profile);
    usrMap.put("picture", this.picture);
    usrMap.put("website", this.website);
    usrMap.put("zoneinfo", this.zoneinfo);
    usrMap.put("locale", this.locale);
    usrMap.put("password", this.password);
    usrMap.put("email", this.email);
    usrMap.put("emailVerified", this.emailVerified);
    usrMap.put("address", this.address);
    usrMap.put("phone", this.phone);
    usrMap.put("phoneNumberVerified", this.phoneNumberVerified);
    usrMap.put("scope", this.scope);
    return usrMap;
  }
}
