package tokyomap.oauth.domain.services.common;

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
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.utils.Logger;

@Component // todo: should be put in the service layer ?
public class TokenScrutiny {

  // todo: define in a config file
  private static final String AUTH_SERVER_HOST = "http://localhost:80";

  // todo: malfunctioning if use `private static final String[] AUDIENCE = new String[] {"http://localhost:9002"};`
  private static final String AUDIENCE = "http://localhost:9002"; // registered resource servers

  private final TokenLogic tokenLogic;
  private final Logger logger;

  @Autowired
  public TokenScrutiny(TokenLogic tokenLogic, Logger logger) {
    this.tokenLogic = tokenLogic;
    this.logger = logger;
  }

  /**
   * scrutinise the given signed JWT
   * @param credentialsDto
   * @param incomingToken
   * @return SignedJWT
   */
  public SignedJWT execute(CredentialsDto credentialsDto, String incomingToken) {

    try {
      SignedJWT signedJWT = SignedJWT.parse(incomingToken);
      this.checkJWSHeader(signedJWT);
      this.checkJWSSignature(signedJWT);

      // check JWT claims
      JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
      if (!jwtClaimsSet.getIssuer().equals(AUTH_SERVER_HOST)) {
        throw new TokenScrutinyFailureException("invalid jwtClaimsSet.iss");
      }
      if (jwtClaimsSet.getAudience().indexOf(AUDIENCE) == -1) {
        throw new TokenScrutinyFailureException("invalid jwtClaimsSet.aud: jwtClaimsSet.getAudience() = " + jwtClaimsSet.getAudience());
      }

      Date now = new Date(System.currentTimeMillis());
      if (now.before(jwtClaimsSet.getIssueTime()) || now.after(jwtClaimsSet.getExpirationTime())) {
        this.logger.log(TokenScrutiny.class.getName(), "jwtClaimsSet.iat or jwtClaimsSet.exp is invalid");
        throw new TokenScrutinyFailureException("jwtClaimsSet.iat or jwtClaimsSet.exp is invalid");
      }
      if (!jwtClaimsSet.getStringClaim("clientId").equals(credentialsDto.getId())) {
        this.logger.log(TokenScrutiny.class.getName(), "invalid jwtClaimsSet.clientId: jwtClaimsSet.getStringClaim(\"clientId\") = " + jwtClaimsSet.getStringClaim("clientId") + ", credentialsDto.getId() = " + credentialsDto.getId());
        throw new TokenScrutinyFailureException("invalid jwtClaimsSet.clientId");
      }

      return signedJWT;

    } catch (ParseException e) {
      this.logger.log(TokenScrutiny.class.getName(), e.getMessage());
      throw new TokenScrutinyFailureException("invalid JWS header");

    } catch (JOSEException e) {
      this.logger.log(TokenScrutiny.class.getName(), e.getMessage());
      throw new TokenScrutinyFailureException("invalid JWS header");

    } catch (Exception e) {
      this.logger.log(TokenScrutiny.class.getName(), e.getMessage());
      throw new TokenScrutinyFailureException(e.getMessage());
    }
  }

  /**
   * scrutinise the given signed JWT except its claims
   * @param incomingToken
   * @return
   */
  public SignedJWT execute(String incomingToken) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(incomingToken);
      this.checkJWSHeader(signedJWT);
      this.checkJWSSignature(signedJWT);
      return signedJWT;

    } catch (ParseException e) {
      this.logger.log(TokenScrutiny.class.getName(), e.getMessage());
      throw new TokenScrutinyFailureException("invalid JWS header");

    } catch (JOSEException e) {
      this.logger.log(TokenScrutiny.class.getName(), e.getMessage());
      throw new TokenScrutinyFailureException("invalid JWS header");

    } catch (Exception e) {
      this.logger.log(TokenScrutiny.class.getName(), e.getMessage());
      throw new TokenScrutinyFailureException(e.getMessage());
    }
  }

  /**
   * check the JWS header of the given signedJWT
   * @param signedJWT
   */
  private void checkJWSHeader(SignedJWT signedJWT) {
    JWSHeader jwsHeader = signedJWT.getHeader();

    if (!jwsHeader.getType().equals(JOSEObjectType.JWT)) {
      this.logger.log(TokenScrutiny.class.getName(), "invalid JWS header type: jwsHeader.getType() =" + jwsHeader.getType());
      throw new TokenScrutinyFailureException("invalid JWS header");
    }

    if (!jwsHeader.getAlgorithm().equals(JWSAlgorithm.RS256)) {
      this.logger.log(TokenScrutiny.class.getName(), "invalid JWS header alg: jwsHeader.getAlgorithm() = " + jwsHeader.getAlgorithm());
      throw new TokenScrutinyFailureException("invalid JWS header");
    }
  }

  /**
   * check the signature of the given signedJWT
   * @param signedJWT
   * @throws JOSEException
   */
  private void checkJWSSignature(SignedJWT signedJWT) throws JOSEException {
    RSAPublicKey rsaPublicKey = this.tokenLogic.getRsaPublicKey();
    JWSVerifier verifier = new RSASSAVerifier(rsaPublicKey);

    if (!signedJWT.verify(verifier)) {
      this.logger.log(TokenScrutiny.class.getName(), "invalid signature");
      throw new TokenScrutinyFailureException("invalid signature");
    }
  }
}
