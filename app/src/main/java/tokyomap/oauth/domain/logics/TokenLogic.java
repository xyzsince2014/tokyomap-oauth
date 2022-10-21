package tokyomap.oauth.domain.logics;

import com.nimbusds.jose.JOSEException;
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
import java.time.ZoneOffset;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.entities.postgres.AccessToken;
import tokyomap.oauth.domain.entities.postgres.RefreshToken;
import tokyomap.oauth.domain.entities.postgres.RsaPublicKey;
import tokyomap.oauth.domain.repositories.postgres.AccessTokenRepository;
import tokyomap.oauth.domain.repositories.postgres.RefreshTokenRepository;
import tokyomap.oauth.domain.repositories.postgres.RsaPublicKeyRepository;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;

@Component
public class TokenLogic {

  // todo: define in rsaKey.properties
  // for dev
//  private static final String AUTH_SERVER_HOST = "http://auth:8080";
//  private static final String AUDIENCE = "http://resource:8081"; // todo: malfunctioning if use `private static final String[] AUDIENCE = new String[] {"http://localhost:9002"};`
  // for prod
  private static final String AUTH_SERVER_HOST = "http://localhost:8080";
  private static final String AUDIENCE = "http://localhost:8081"; // todo: malfunctioning if use `private static final String[] AUDIENCE = new String[] {"http://localhost:9002"};`

  private static final int ACCESS_TOKEN_LIFETIME = 30;
  private static final int REFRESH_TOKEN_LIFETIME = 90;
  private static final int ID_TOKEN_LIFETIME = 60;
  private static final String ALGORITHM = "RSA";
  private static final int KEY_SIZE = 2048;

  private final AccessTokenRepository accessTokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final RsaPublicKeyRepository rsaPublicKeyRepository;

  private RSAPublicKey rsaPublicKey;
  private RSAPrivateKey rsaPrivateKey;
  private String kid;

  @Autowired
  public TokenLogic(AccessTokenRepository accessTokenRepository, RefreshTokenRepository refreshTokenRepository, RsaPublicKeyRepository rsaPublicKeyRepository) {
    this.accessTokenRepository = accessTokenRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.rsaPublicKeyRepository = rsaPublicKeyRepository;

    try {
      KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(ALGORITHM);
      keyGenerator.initialize(KEY_SIZE);
      KeyPair kp = keyGenerator.genKeyPair();
      this.rsaPublicKey = (RSAPublicKey) kp.getPublic();
      this.rsaPrivateKey = (RSAPrivateKey) kp.getPrivate();

      // the JSON Web Key (JWK public key)
      RSAKey jwk = new RSAKey.Builder(this.rsaPublicKey).keyIDFromThumbprint().build();
      this.kid = jwk.getKeyID();

      LocalDateTime now = LocalDateTime.now();
      this.rsaPublicKeyRepository.saveAndFlush(new RsaPublicKey(this.kid, this.rsaPublicKey, now, now));

    } catch (NoSuchAlgorithmException e) {
      // todo:
    } catch (JOSEException e) {
      // todo:
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

  // todo: do not delete, update t_access(refesh)_token.expires_at instead
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
   * @param scopes
   * @param isRefreshTokenGenerated
   * @param nonce
   * @return GenerateTokensResponseDto
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public GenerateTokensResponseDto generateTokens(String clientId, String sub,String[] scopes, Boolean isRefreshTokenGenerated, String nonce) throws Exception {

    LocalDateTime now = LocalDateTime.now();

    SignedJWT accessJWT = this.createSignedJWT(sub, RandomStringUtils.random(8, true, true), scopes, clientId, now, ACCESS_TOKEN_LIFETIME);

    // Open ID Connect ID token
    SignedJWT idJWT = this.createIdJWT(sub, clientId, nonce, now, ID_TOKEN_LIFETIME, now); // todo: authTime should be set on authentication

    if(!isRefreshTokenGenerated) {
      AccessToken accessTokenRegistered = this.accessTokenRepository.saveAndFlush(new AccessToken(accessJWT.serialize(), now, now));
      // scopes must not be sent back to the client in production
      GenerateTokensResponseDto responseDto = new GenerateTokensResponseDto("Bearer",accessTokenRegistered.getAccessToken(), null, idJWT.serialize(), String.join(" ", scopes));
      return responseDto;
    }

    SignedJWT refreshJWT = this.createSignedJWT(sub, RandomStringUtils.random(8, true, true), scopes, clientId, now, REFRESH_TOKEN_LIFETIME);

    AccessToken accessTokenRegistered = this.accessTokenRepository.saveAndFlush(new AccessToken(accessJWT.serialize(), now, now));
    RefreshToken refreshTokenRegistered = this.refreshTokenRepository.saveAndFlush(new RefreshToken(refreshJWT.serialize(), now, now));

    // todo: scope must not be sent back to the client in production
    GenerateTokensResponseDto responseDto = new GenerateTokensResponseDto(
        "Bearer",
        accessTokenRegistered.getAccessToken(),
        refreshTokenRegistered.getRefreshToken(),
        idJWT.serialize(),
        String.join(" ", scopes)
    );

    return responseDto;
  }

  /**
   * create a JWSHeader
   * @return JWSHeader
   * @throws Exception
   */
  private JWSHeader createJWSHeader() throws Exception {

    // the JSON Web Signature Header (JWS Header)
    JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256) // the signature algorithm is the RS256
        .keyID(this.kid) // use the RsaPublicKey Thumbprint as kid
        .type(JOSEObjectType.JWT) // the type of the token
        .build();

    return jwsHeader;
  }

  /**
   * create a signed JWT
   * @param sub
   * @param scopes
   * @param clientId
   * @return SignedJWT
   * @throws Exception
   */
  private SignedJWT createSignedJWT(String sub, String jti, String[] scopes, String clientId, LocalDateTime iat, long days) throws Exception {

    JWSHeader jwsHeader = this.createJWSHeader();

    // payload
    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .claim("iss", AUTH_SERVER_HOST) // the issuer, normally the URI of the auth server
        .claim("sub", sub) // the subject, normally the unique identifier for the resource owner
        .claim("aud", AUDIENCE) // the audience, normally the URI(s) of the protected resource(s) the access token can be sent to
        .claim("iat", iat.toEpochSecond(ZoneOffset.ofHours(+9))) // the issued-at timestamp of the token in seconds from 1 Jan 1970 (GMT)
        .claim("exp", iat.plusDays(days).toEpochSecond(ZoneOffset.ofHours(+9))) // the expiration time, the token expires in 5 min later in this case
        .claim("jti", jti) // the unique identifier of the token, that is a value unique to each token created by the issuer, and it’s often a cryptographically random value
        .claim("scopes", scopes)
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
  private SignedJWT createIdJWT(String sub, String clientId, String nonce, LocalDateTime iat, long minutes, LocalDateTime authTime) throws Exception {

    // payload
    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .claim("iss", AUTH_SERVER_HOST) // the issuer of the token, i.e. the URL of the ID Provider
        .claim("sub", sub) // the subject of the token, a stable and unique identifier for the user at the ID Provider, which is usually a machine-readable string and shouldn’t be used as a username
        .claim("aud", clientId) // the audience of the id token that must contain the client ID of the Relying Party
        .claim("iat", iat.toEpochSecond(ZoneOffset.ofHours(+9))) // the timestamp at which the token is issued
        .claim("exp", iat.plusMinutes(minutes).toEpochSecond(ZoneOffset.ofHours(+9))) // the expiration timestamp of the token at which all ID tokens expire and usually pretty quickly
        .claim("nonce", nonce) // a string sent by the Relying Party during the authentication request, used to mitigate replay attacks. It must be included if the Relying Party sends it
        .claim("authTime", authTime.toEpochSecond(ZoneOffset.ofHours(+9))) // the timestamp at which the user authenticated to the Id Provider
        .claim("amr", new String[] {"pwd"}) // the authentication method reference, which indicates how the user authenticated to the Id Provider, e.g. pwd (by password), otp (by password and one-time password), sms (by SMS), email (by mail)
        // todo: .claim("atHash", accessToken)
        // todo: .claim("cHash", hashed authorisation code)
        .build();

    SignedJWT signedJWT = new SignedJWT(this.createJWSHeader(), jwtClaimsSet);
    RSASSASigner signer = new RSASSASigner(this.rsaPrivateKey);
    signedJWT.sign(signer);

    return signedJWT;
  }

  /**
   * get the RSAPublicKey for the given kid
   * @param kid
   * @return RSAPublicKey
   */
  public RSAPublicKey getRsaPublicKeyByKid(String kid) {
    Optional<RsaPublicKey> rsaPublicKeyOptional = this.rsaPublicKeyRepository.findById(kid);
    if (rsaPublicKeyOptional == null) {
      return null;
    }
    return rsaPublicKeyOptional.get().getRsaPublicKey();
  }
}
