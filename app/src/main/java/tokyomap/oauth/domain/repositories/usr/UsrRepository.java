package tokyomap.oauth.domain.repositories.usr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tokyomap.oauth.domain.entities.Usr;

@Repository
public interface UsrRepository extends JpaRepository<Usr, String> {}
