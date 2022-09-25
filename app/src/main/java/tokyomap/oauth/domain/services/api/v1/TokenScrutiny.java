package tokyomap.oauth.domain.services.api.v1;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.dtos.CredentialsDto;

@Component
public class TokenScrutiny {

  // todo: define in a config file
  private static final String AUTH_SERVER_HOST = "http://auth:8080";

  // todo: malfunctioning if use `private static final String[] AUDIENCE = new String[] {"http://localhost:9002"};`
  private static final String AUDIENCE = "http://resource:8081"; // registered resource servers

  private final TokenLogic tokenLogic;

  @Autowired
  public TokenScrutiny(TokenLogic tokenLogic) { this.tokenLogic = tokenLogic; }

  /**
   * scrutinise the given signed JWT
   * @param credentialsDto
   * @param incomingToken
   * @return SignedJWT
   */
  public SignedJWT execute(CredentialsDto credentialsDto, String incomingToken) throws ApiException {

    try {
      SignedJWT signedJWT = SignedJWT.parse(incomingToken);
      this.checkJWSHeader(signedJWT);
      this.checkJWSSignature(signedJWT);

      // check JWT claims
      JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
      if (!jwtClaimsSet.getIssuer().equals(AUTH_SERVER_HOST)) {
        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Token Issuer");
      }
      if (jwtClaimsSet.getAudience().indexOf(AUDIENCE) == -1) {
        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Token Audience");
      }

      Date now = new Date(System.currentTimeMillis());
      if (now.before(jwtClaimsSet.getIssueTime()) || now.after(jwtClaimsSet.getExpirationTime())) {
        throw new ApiException(HttpStatus.UNAUTHORIZED, "Token Expired");
      }
      if (!jwtClaimsSet.getStringClaim("clientId").equals(credentialsDto.getId())) {
        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Client Id");
      }

      return signedJWT;

    } catch (ParseException e) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Token");

    } catch (JOSEException e) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Token");

    } catch (Exception e) {
      throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  /**
   * scrutinise the given signed JWT except its claims
   * @param incomingToken
   * @return
   */
  public SignedJWT execute(String incomingToken) throws ApiException {
    try {
      SignedJWT signedJWT = SignedJWT.parse(incomingToken);
      this.checkJWSHeader(signedJWT);
      this.checkJWSSignature(signedJWT);
      return signedJWT;

    } catch (ParseException e) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Token");

    } catch (JOSEException e) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Token");

    } catch (Exception e) {
      throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  /**
   * check the JWS header of the given signedJWT
   * @param signedJWT
   */
  private void checkJWSHeader(SignedJWT signedJWT) throws ApiException {
    JWSHeader jwsHeader = signedJWT.getHeader();
    if (!jwsHeader.getType().equals(JOSEObjectType.JWT)) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Token");
    }
    if (!jwsHeader.getAlgorithm().equals(JWSAlgorithm.RS256)) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid Token");
    }
  }

  /**
   * check the signature of the given signedJWT
   * @param signedJWT
   * @throws JOSEException
   */
  private void checkJWSSignature(SignedJWT signedJWT) throws JOSEException {
    RSAPublicKey rsaPublicKey = this.tokenLogic.getRsaPublicKeyByKid(signedJWT.getHeader().getKeyID());
    JWSVerifier verifier = new RSASSAVerifier(rsaPublicKey);
    signedJWT.verify(verifier);
  }
}
