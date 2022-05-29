package tokyomap.oauth.domain.logics;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.entities.postgres.AccessToken;
import tokyomap.oauth.domain.entities.postgres.RefreshToken;
import tokyomap.oauth.domain.repositories.postgres.AccessTokenRepository;
import tokyomap.oauth.domain.repositories.postgres.RefreshTokenRepository;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;

@Component
public class TokenLogic {

  private static final String ISSUER = "http://localhost:80";
  private static final String[] AUDIENCE = new String[] {"http://localhost:9002"};

  private final AccessTokenRepository accessTokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  private RSAPublicKey rsaPublicKey;
  private RSAPrivateKey rsaPrivateKey;

  @Autowired
  public TokenLogic(AccessTokenRepository accessTokenRepository, RefreshTokenRepository refreshTokenRepository) {
    this.accessTokenRepository = accessTokenRepository;
    this.refreshTokenRepository = refreshTokenRepository;

    // todo: fetch keys from DB
    try {
      KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
      keyGenerator.initialize(2048);
      KeyPair kp = keyGenerator.genKeyPair();
      // todo: this.rsaPublicKey = (RSAPublicKey) kp.getPublic();
      this.rsaPublicKey = (RSAPublicKey) kp.getPublic();
      this.rsaPrivateKey = (RSAPrivateKey) kp.getPrivate();
    } catch (Exception e) {
      }
    }

  /**
   * get the AccessToken entity for the given access token
   * @param accessToken
   * @return AccessToken
   */
  public AccessToken getAccessToken(String accessToken) {
    Optional<AccessToken> optionalAccessToken = this.accessTokenRepository.findById(accessToken);
    return optionalAccessToken.orElse(null);
  }

  /**
   * get the RefreshToken entity for the given refresh token
   * @param refreshToken
   * @return RefreshToken
   */
  public RefreshToken getRefreshToken(String refreshToken) {
    Optional<RefreshToken> optionalRefreshToken = this.refreshTokenRepository.findById(refreshToken);
    return optionalRefreshToken.orElse(null);
  }

  /**
   * delete the AccessToken and the RefreshToken
   * @param accessToken
   * @param refreshToken
   */
  public void revokeTokens(String accessToken, String refreshToken) {
    this.accessTokenRepository.deleteById(accessToken);
    this.refreshTokenRepository.deleteById(refreshToken);
  }

  /**
   * generate JWT and signing them with RSA private key
   * @param clientId
   * @param sub
   * @param scope
   * @param isRefreshTokenGenerated
   * @param nonce
   * @return GenerateTokensResponseDto
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public GenerateTokensResponseDto generateTokens(String clientId, String sub,String[] scope, Boolean isRefreshTokenGenerated, String nonce) throws Exception {

    SignedJWT accessJWT = this.createSignedJWT(sub, this.AUDIENCE, RandomStringUtils.random(8, true, true), scope, clientId);

    // Open ID Connect ID token
    SignedJWT idJWT = this.createIdJWT(sub, clientId, nonce);

    if(!isRefreshTokenGenerated) {
      // todo: registration error handling
      AccessToken accessTokenRegistered = this.accessTokenRepository.saveAndFlush(new AccessToken(accessJWT.serialize()));
      // scope must not be sent back to the client in production
      GenerateTokensResponseDto responseDto = new GenerateTokensResponseDto("Bearer",accessTokenRegistered.getAccessToken(), null, idJWT.serialize(), String.join(" ", scope));
      return responseDto;
    }

    SignedJWT refreshJWT = this.createSignedJWT(sub, this.AUDIENCE, RandomStringUtils.random(8, true, true), scope, clientId);

    // todo: registration error handling
    AccessToken accessTokenRegistered = this.accessTokenRepository.saveAndFlush(new AccessToken(accessJWT.serialize()));
    RefreshToken refreshTokenRegistered = this.refreshTokenRepository.saveAndFlush(new RefreshToken(refreshJWT.serialize()));

    // scope must not be sent back to the client in production
    GenerateTokensResponseDto responseDto = new GenerateTokensResponseDto(
        "Bearer",
        accessTokenRegistered.getAccessToken(),
        refreshTokenRegistered.getRefreshToken(),
        idJWT.serialize(),
        String.join(" ", scope)
    );

    return responseDto;
  }

  /**
   * create a JWSHeader
   * @return JWSHeader
   * @throws Exception
   */
  private JWSHeader createJWSHeader() throws Exception {
    // the JSON Web Key (JWK)
    RSAKey jwk = new RSAKey.Builder(this.rsaPublicKey).keyIDFromThumbprint().build();

    // the JSON Web Signature Header (JWS Header)
    JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256) // the signature algorithm is the RS256
        .keyID(jwk.getKeyID()) // use the JWK Thumbprint as kid
        .type(JOSEObjectType.JWT) // the type of the token
        .build();

    return jwsHeader;
  }

  /**
   * create a signed JWT
   * @param sub
   * @param aud
   * @param scope
   * @param clientId
   * @return SignedJWT
   * @throws Exception
   */
  private SignedJWT createSignedJWT(String sub, String[] aud, String jti, String[] scope, String clientId) throws Exception {

    LocalDateTime ldt = LocalDateTime.now();

    JWSHeader jwsHeader = this.createJWSHeader();

    // payload
    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .claim("iss", this.ISSUER) // the issuer, normally the URI of the auth server
        .claim("sub", sub) // the subject, normally the unique identifier for the resource owner
        .claim("aud", this.AUDIENCE) // the audience, normally the URI(s) of the protected resource(s) the access token can be sent to
        .claim("iat", ldt.toString()) // the issued-at timestamp of the token in seconds from 1 Jan 1970 (GMT)
        // todo: fix exp
        .claim("exp", ldt.toString()) // the expiration time, the token expires in 5 min later in this case
        .claim("jti", jti) // the unique identifier of the token, that is a value unique to each token created by the issuer, and it’s often a cryptographically random value
        .claim("scope", scope) // String[] scope
        .claim("clientId", clientId)
        .build();

    SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
    RSASSASigner signer = new RSASSASigner(this.rsaPrivateKey);
    signedJWT.sign(signer);

    return signedJWT;
  }

  /**
   * create an ID JWT
   * @param sub
   * @param clientId
   * @param nonce
   * @return SignedJWT
   * @throws Exception
   */
  private SignedJWT createIdJWT(String sub, String clientId, String nonce) throws Exception {

    LocalDateTime ldt = LocalDateTime.now();

    // payload
    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .claim("iss", this.ISSUER) // the issuer of the token, i.e. the URL of the Id Provider
        .claim("sub", sub) // the subject of the token, a stable and unique identifier for the user at the Id Provider, which is usually a machine-readable string and shouldn’t be used as a username
        .claim("aud", clientId) // the audience of the token that must contain the client ID of the Relying Party
        .claim("iat", ldt.toString()) // the timestamp at which the token is issued
        // todo: fix exp
        .claim("exp", ldt.toString()) // the expiration timestamp of the token at which all ID tokens expire and usually pretty quickly
        .claim("nonce", nonce) // a string sent by the Relying Party during the authentication request, used to mitigate replay attacks similar to the state parameter. It must be included if the Relying Party sends it
        .build();

    SignedJWT signedJWT = new SignedJWT(this.createJWSHeader(), jwtClaimsSet);
    RSASSASigner signer = new RSASSASigner(this.rsaPrivateKey);
    signedJWT.sign(signer);

    return signedJWT;
  }
}
