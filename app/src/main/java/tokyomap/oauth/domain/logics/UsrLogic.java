package tokyomap.oauth.domain.logics;

import java.util.List;
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
  public Usr getUsrBySub(String sub) {
    Optional<Usr> optionalUsr = this.usrRepository.findById(sub);
    return optionalUsr.orElse(null);
  }

  /**
   * get all usrs
   * @return usrList
   */
  public List<Usr> getAllUsrs() {
    return this.usrRepository.findAll();
  }

  /**
   * register the given usr
   * @param usr
   * @return usr saved
   */
  public Usr register(Usr usr) {
    return this.usrRepository.save(usr);
  }

  /**
   * delete the usr by sub
   * @param sub
   */
  public void deleteUsrBySub(String sub) {
    this.usrRepository.deleteById(sub);
  }
}
