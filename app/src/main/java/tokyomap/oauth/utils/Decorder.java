package tokyomap.oauth.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import tokyomap.oauth.dtos.CredentialsDto;

@Component
public class Decorder {

  /**
   * decode an authorization header to credentials
   * @param authorization
   * @return CredentialsDto
   */
  public CredentialsDto decodeCredentials(String authorization) {
    if(authorization == null) {
      return null;
    }

    Base64 base64 = new Base64();
    String encodedClientCredentials = authorization.substring("Basic ".length());
    String decodedString = new String(base64.decode(encodedClientCredentials.getBytes()));
    String[] splitString = decodedString.split(":");

    CredentialsDto clientCredentials = new CredentialsDto(splitString[0], splitString[1]);

    return clientCredentials;
  }
}
