package tokyomap.oauth.application.publicKeys;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.domain.services.publicKeys.RetrieveRsaPublicKeyService;
import tokyomap.oauth.dtos.RetrieveRsaPublicKeyResponseDto;

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
  public ResponseEntity<RetrieveRsaPublicKeyResponseDto> retrieveRsaPublicKey(@RequestParam Map<String, String> queryParams) {

    try {
      String pemPublicKey = this.retrieveRsaPublicKeyService.execute(queryParams.get("kid"));
      return ResponseEntity.status(HttpStatus.OK).body(new RetrieveRsaPublicKeyResponseDto(pemPublicKey));

    } catch (Exception e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
