package tokyomap.oauth.domain.services.token;

import com.nimbusds.jose.util.Base64URL;
import java.security.MessageDigest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.entities.redis.ProAuthoriseCache;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.RedisLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.logics.UsrLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

@Service
public class AuthorisationCodeFlowSerivice extends TokenService<ProAuthoriseCache> {

  private final RedisLogic redisLogic;
  private final TokenLogic tokenLogic;
  private final UsrLogic usrLogic;
  private final Logger logger;

  @Autowired
  public AuthorisationCodeFlowSerivice(
      ClientLogic clientLogic, Decorder decorder, RedisLogic redisLogic, TokenLogic tokenLogic, UsrLogic usrLogic, Logger logger) {
    super(clientLogic, decorder, logger);
    this.redisLogic = redisLogic;
    this.tokenLogic = tokenLogic;
    this.usrLogic = usrLogic;
    this.logger = logger;
  }

  /**
   * execute validation of request to the token endpoint
   * @return TokenValidationResultDto
   */
  @Override
  public TokenValidationResultDto<ProAuthoriseCache> execValidation(GenerateTokensRequestDto requestDto, String authorization) {

    CredentialsDto credentialsDto = this.validateClient(requestDto, authorization);
    ProAuthoriseCache proAuthoriseCache = this.redisLogic.getProAuthoriseCache(requestDto.getCode());

    if (proAuthoriseCache == null) {
      this.logger.log(AuthorisationCodeFlowSerivice.class.getName(),"no proAuthoriseCache");
      throw new InvalidTokenRequestException("invalid code.");
    }

    if (!credentialsDto.getId().equals(proAuthoriseCache.getPreAuthoriseCache().getClientId())) {
      this.logger.log(AuthorisationCodeFlowSerivice.class.getName(), "invalid client id: proAuthoriseCache.getPreAuthoriseCache().getClientId() = " + proAuthoriseCache.getPreAuthoriseCache().getClientId() + ", credentialsDto.getClientId() = " + credentialsDto.getId());
      throw new InvalidTokenRequestException("invalid clientId.");
    }

    /* *** check PKCE values, cf. https://auth0.com/docs/authorization/flows/authorization-code-flow-with-proof-key-for-code-exchange-pkce *** */
    if (proAuthoriseCache.getPreAuthoriseCache().getCodeChallenge() == null) {
      this.logger.log(AuthorisationCodeFlowSerivice.class.getName(), "invalid codeChallenge");
      throw new InvalidTokenRequestException("invalid codeChallenge.");
    }

    String codeChallengeMethod = proAuthoriseCache.getPreAuthoriseCache().getCodeChallengeMethod();
    if (!codeChallengeMethod.equals("SHA256")) {
      this.logger.log(AuthorisationCodeFlowSerivice.class.getName(), "invalid codeChallengeMethod: codeChallengeMethod = " + codeChallengeMethod);
      throw new InvalidTokenRequestException("invalid codeChallengeMethod.");
    }

    // recreate the codeChallenge from `requestDto.getCodeVerifier()`
    MessageDigest md = DigestUtils.getSha256Digest();
    md.update(requestDto.getCodeVerifier().getBytes());
    String codeChallenge = Base64URL.encode(md.digest()).toString();

    if (!proAuthoriseCache.getPreAuthoriseCache().getCodeChallenge().equals(codeChallenge)) {
      this.logger.log(AuthorisationCodeFlowSerivice.class.getName(), "codeChallenge = " + codeChallenge + ", proAuthoriseCache.getPreAuthoriseCache().getCodeChallenge() = " + proAuthoriseCache.getPreAuthoriseCache().getCodeChallenge());
      throw new InvalidTokenRequestException("invalid codeChallenge");
    }

    return new TokenValidationResultDto(credentialsDto.getId(), proAuthoriseCache);
  }

  /**
   * generate tokens
   * @param tokenValidationResultDto
   * @return GenerateTokensResponseDto
   */
  @Override
  @Transactional
  public GenerateTokensResponseDto generateTokens(TokenValidationResultDto<ProAuthoriseCache> tokenValidationResultDto) {

    Usr usr = this.usrLogic.getUsrBySub(tokenValidationResultDto.getPayload().getSub());
    if(usr == null) {
      throw new InvalidTokenRequestException("no matching usr");
    }

    this.logger.log(AuthorisationCodeFlowSerivice.class.getName(), "tokenValidationResultDto.payload = " + tokenValidationResultDto.getPayload().toString());

    try {
      GenerateTokensResponseDto responseDto = this.tokenLogic.generateTokens(
          tokenValidationResultDto.getClientId(), usr.getSub(),
          tokenValidationResultDto.getPayload().getScopeRequested(),true, null
      );
      return responseDto;
    } catch (Exception e) {
      throw new InvalidTokenRequestException(e.getMessage());
    }
  }
}
