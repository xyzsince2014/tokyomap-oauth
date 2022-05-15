package tokyomap.oauth.domain.logics;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.repositories.postgres.UsrRepository;

@Component
public class UsrLogic {

  private final UsrRepository usrRepository;

  @Autowired
  public UsrLogic(UsrRepository usrRepository) {
    this.usrRepository = usrRepository;
  }

  /**
   * get a usr for the given sub
   * @param sub
   * @return
   */
  public Optional<Usr> getUsrBySub(String sub) {
    return this.usrRepository.findById(sub);
  }
}
