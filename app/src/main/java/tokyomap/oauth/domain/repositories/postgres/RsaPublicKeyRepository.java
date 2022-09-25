package tokyomap.oauth.domain.repositories.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import tokyomap.oauth.domain.entities.postgres.RsaPublicKey;

public interface RsaPublicKeyRepository extends JpaRepository<RsaPublicKey, String> {}
