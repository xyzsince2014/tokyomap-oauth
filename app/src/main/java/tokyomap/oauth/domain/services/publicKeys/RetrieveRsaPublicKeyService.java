package tokyomap.oauth.domain.services.publicKeys;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.logics.TokenLogic;

@Service
public class RetrieveRsaPublicKeyService {

  private final Encoder encodeder = Base64.getEncoder();

  private final TokenLogic tokenLogic;

  @Autowired
  public RetrieveRsaPublicKeyService(TokenLogic tokenLogic) {
    this.tokenLogic = tokenLogic;
  }

  /**
   * get the RSAPublicKey for the given kid
   * @param kid
   * @return the PEM encoded public key
   */
  public String execute(String kid) {
    RSAPublicKey rsaPublicKey = this.tokenLogic.getRsaPublicKeyByKid(kid);
    byte[] encoded = encodeder.encode(rsaPublicKey.getEncoded());

    int index = 0;
    StringBuilder sb = new StringBuilder(encoded.length + 20);
    while (index < encoded.length) {
      int len = Math.min(64, encoded.length - index);
      if (index > 0) {
        sb.append("\n");
      }
      sb.append(new String(encoded, index, len, Charsets.UTF_8));
      index += len;
    }

    // todo: the format should be a constant
    return String.format("-----BEGIN PUBLIC KEY-----%n%s%n-----END PUBLIC KEY-----%n", sb);
  }
}
