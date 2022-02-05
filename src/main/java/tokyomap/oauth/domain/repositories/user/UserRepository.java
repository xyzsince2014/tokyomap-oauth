package tokyomap.oauth.domain.repositories.user;

import tokyomap.oauth.domain.models.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  User findByUserId(String userId);
}
