package tokyomap.oauth.application.publicKeys;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.domain.services.publicKeys.RetrieveRsaPublicKeyService;

@RestController
@RequestMapping("/public-keys")
public class PublicKeysRestController {

  private final RetrieveRsaPublicKeyService retrieveRsaPublicKeyService;

  @Autowired
  public PublicKeysRestController(RetrieveRsaPublicKeyService retrieveRsaPublicKeyService) {
    this.retrieveRsaPublicKeyService = retrieveRsaPublicKeyService;
  }

  /**
   * retrieve a RSAPublicKey
   * @return the PEM encoded public key
   */
  @RequestMapping(method = RequestMethod.GET)
  public String retrieveRsaPublicKey(@RequestParam Map<String, String> queryParams) {
    String pemPublicKey = this.retrieveRsaPublicKeyService.execute(queryParams.get("kid"));
    return pemPublicKey;
  }
}
