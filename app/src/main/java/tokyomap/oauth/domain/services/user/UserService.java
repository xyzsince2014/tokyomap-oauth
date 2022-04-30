package tokyomap.oauth.domain.services.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.models.entities.Role;
import tokyomap.oauth.domain.models.entities.User;
import tokyomap.oauth.domain.repositories.user.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public User findUserByUserId(String userId) throws Exception {
    User user = this.userRepository.findByUserId(userId);

    if(user == null) {
      throw new Exception("User Not Found, userId = " + userId);
    }

    return user;
  }

  public List<User> findAdmins() {
    return this.userRepository.findByRoleOrderByUserId(Role.ADMIN);
  }

  public User saveUser(User user) {
    User userCreated = this.userRepository.save(user);
    return userCreated;
  }

  public void deleteUserByUserId(String userId) {
    this.userRepository.deleteById(userId);
  }
}
