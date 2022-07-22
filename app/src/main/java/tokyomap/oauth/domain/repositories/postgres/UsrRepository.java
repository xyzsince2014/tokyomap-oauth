package tokyomap.oauth.domain.repositories.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tokyomap.oauth.domain.entities.postgres.Usr;

@Repository
public interface UsrRepository extends JpaRepository<Usr, String> {
  Usr findByEmail(String email);
}
