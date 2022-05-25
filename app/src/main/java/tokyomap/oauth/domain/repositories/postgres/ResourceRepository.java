package tokyomap.oauth.domain.repositories.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tokyomap.oauth.domain.entities.postgres.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {}
