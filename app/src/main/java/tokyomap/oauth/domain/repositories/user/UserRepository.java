package tokyomap.oauth.domain.repositories.user;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tokyomap.oauth.domain.models.entities.Role;
import tokyomap.oauth.domain.models.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

  @Cacheable("user")
  User findByUserId(String userId);

  @Cacheable("users")
  List<User> findByRoleOrderByUserId(Role role);
}
