package tokyomap.oauth.domain.services.usr;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.repositories.postgres.UsrRepository;

@Service
public class UsrDominService {

  private final UsrRepository usrRepository;

  @Autowired
  public UsrDominService(UsrRepository usrRepository) {
    this.usrRepository = usrRepository;
  }

  /**
   * find the usr for the given sub
   * @param sub
   * @return usr
   */
  public Usr findUsrBySub(String sub) {
    Optional<Usr> optionalUsr = this.usrRepository.findById(sub);
    return optionalUsr.orElse(null);
  }

  /**
   * find all usrs
   * @return usrList
   */
  public List<Usr> findAll() {
    return this.usrRepository.findAll();
  }

  /**
   * save the usr
   * @param usr
   * @return usr saved
   */
  public Usr save(Usr usr) {
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
