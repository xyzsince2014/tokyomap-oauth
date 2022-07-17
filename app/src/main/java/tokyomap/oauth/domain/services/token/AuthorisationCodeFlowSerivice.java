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

@Service
public class AuthorisationCodeFlowSerivice extends TokenService<ProAuthoriseCache> {

  private final RedisLogic redisLogic;
  private final TokenLogic tokenLogic;
  private final UsrLogic usrLogic;

  @Autowired
  public AuthorisationCodeFlowSerivice(
      ClientLogic clientLogic, Decorder decorder, RedisLogic redisLogic, TokenLogic tokenLogic, UsrLogic usrLogic) {
    super(clientLogic, decorder);
    this.redisLogic = redisLogic;
    this.tokenLogic = tokenLogic;
    this.usrLogic = usrLogic;
  }

  /**
   * execute validation of request to the token endpoint
   * @return TokenValidationResultDto
   */
  @Override
  public TokenValidationResultDto<ProAuthoriseCache> execValidation(GenerateTokensRequestDto requestDto, String authorization) throws InvalidTokenRequestException {

    CredentialsDto credentialsDto = this.validateClient(requestDto, authorization);
    ProAuthoriseCache proAuthoriseCache = this.redisLogic.getProAuthoriseCache(requestDto.getCode());

    if (proAuthoriseCache == null) {
      throw new InvalidTokenRequestException("Invalid Authorisation Code");
    }

    if (!credentialsDto.getId().equals(proAuthoriseCache.getPreAuthoriseCache().getClientId())) {
      throw new InvalidTokenRequestException("Invalid Client Id");
    }

    /* *** check PKCE values, cf. https://auth0.com/docs/authorization/flows/authorization-code-flow-with-proof-key-for-code-exchange-pkce *** */
    if (proAuthoriseCache.getPreAuthoriseCache().getCodeChallenge() == null) {
      throw new InvalidTokenRequestException("Invalid Code Challenge");
    }

    String codeChallengeMethod = proAuthoriseCache.getPreAuthoriseCache().getCodeChallengeMethod();
    if (!codeChallengeMethod.equals("SHA256")) {
      throw new InvalidTokenRequestException("Invalid Code Challenge Method");
    }

    // recreate the codeChallenge from `requestDto.getCodeVerifier()`
    MessageDigest md = DigestUtils.getSha256Digest();
    md.update(requestDto.getCodeVerifier().getBytes());
    String codeChallenge = Base64URL.encode(md.digest()).toString();

    if (!proAuthoriseCache.getPreAuthoriseCache().getCodeChallenge().equals(codeChallenge)) {
      throw new InvalidTokenRequestException("Invalid Code Challenge");
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
  public GenerateTokensResponseDto execute(TokenValidationResultDto<ProAuthoriseCache> tokenValidationResultDto) throws Exception {

    Usr usr = this.usrLogic.getUsrBySub(tokenValidationResultDto.getPayload().getSub());
    if(usr == null) {
      throw new InvalidTokenRequestException("No Matching User");
    }

    GenerateTokensResponseDto responseDto = this.tokenLogic.generateTokens(
        tokenValidationResultDto.getClientId(), usr.getSub(),
        tokenValidationResultDto.getPayload().getScopeRequested(),true,
        tokenValidationResultDto.getPayload().getPreAuthoriseCache().getNonce()
    );

    return responseDto;
  }
}
