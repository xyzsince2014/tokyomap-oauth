package tokyomap.oauth.domain.services.authenticate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.logics.RedisLogic;
import tokyomap.oauth.domain.logics.UsrLogic;

@Service
public class AuthenticateService implements UserDetailsService {

  private final RedisLogic redisLogic;
  private final UsrLogic usrLogic;

  @Autowired
  public AuthenticateService(RedisLogic redisLogic, UsrLogic usrLogic) {
    this.redisLogic = redisLogic;
    this.usrLogic = usrLogic;
  }

  /**
   * load user by email instead of usename
   * @param email
   * @return ResourceOwnerDetails
   * @throws UsernameNotFoundException
   */
  @Override
  public ResourceOwnerDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Usr usr = this.usrLogic.getUsrByEmail(email);
    if(usr == null) {
      throw new UsernameNotFoundException("Resource Owner Not Found");
    }
    return new ResourceOwnerDetails(usr);
  }
}
