package tokyomap.oauth.domain.services.user;

import tokyomap.oauth.domain.models.entities.User;
import tokyomap.oauth.domain.repositories.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ReservationUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Autowired
  public ReservationUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = this.userRepository.findByUserId(username);
    if(user == null) {
      throw new UsernameNotFoundException(username + " not found");
    }
    return new ReservationUserDetails(user);
  }

}
