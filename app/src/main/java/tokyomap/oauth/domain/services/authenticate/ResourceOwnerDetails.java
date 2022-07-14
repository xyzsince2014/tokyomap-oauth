package tokyomap.oauth.domain.services.authenticate;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import tokyomap.oauth.domain.entities.postgres.Usr;

public class ResourceOwnerDetails implements UserDetails {

  private final Usr resourceOwner;

  @Autowired
  public ResourceOwnerDetails(Usr usr) {
    this.resourceOwner = usr;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return AuthorityUtils.createAuthorityList(this.resourceOwner.getRole().name());
  }

  @Override
  public String getPassword() {
    return this.resourceOwner.getPassword();
  }

  /**
   * use usr.name as the Resource Owner's username
   * @return username
   */
  @Override
  public String getUsername() {
    return this.resourceOwner.getName();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public Usr getResourceOwner() {
    return this.resourceOwner;
  }

  @Override
  public String toString() {
    return "resourceOwner = " + this.resourceOwner.toString();
  }
}
