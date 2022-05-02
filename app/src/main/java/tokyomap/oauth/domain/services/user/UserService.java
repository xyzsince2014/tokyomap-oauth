package tokyomap.oauth.domain.services.user;

import java.util.List;
import tokyomap.oauth.aspects.UserNotFoundException;
import tokyomap.oauth.domain.models.entities.Role;
import tokyomap.oauth.domain.models.entities.User;
import tokyomap.oauth.domain.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User findUserByUserId(String userId) throws UserNotFoundException {
    User user = this.userRepository.findByUserId(userId);

    if(user == null) {
      throw new UserNotFoundException("User Not Found, userId = " + userId);
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
