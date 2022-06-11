package tokyomap.oauth.domain.services.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.entities.redis.ProAuthoriseCache;
import tokyomap.oauth.domain.logics.AuthCodeLogic;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.logics.UsrLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

@Service
public class AuthorisationCodeFlowDomainSerivice extends TokenDomainService<ProAuthoriseCache> {

  private final AuthCodeLogic authCodeLogic;
  private final TokenLogic tokenLogic;
  private final UsrLogic usrLogic;
  private final Logger logger;

  @Autowired
  public AuthorisationCodeFlowDomainSerivice(ClientLogic clientLogic, Decorder decorder, AuthCodeLogic authCodeLogic, TokenLogic tokenLogic, UsrLogic usrLogic, Logger logger) {
    super(clientLogic, decorder, logger);
    this.authCodeLogic = authCodeLogic;
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
    ProAuthoriseCache proAuthoriseCache = this.authCodeLogic.getCacheByCode(requestDto.getCode());

    //  check the expiry date of the auth code here
    if (!credentialsDto.getId().equals(proAuthoriseCache.getAuthReqParams().getClientId())) {
      this.logger.log(
          "AuthorisationCodeFlowDomainSerivice",
          "invalid client id: proAuthoriseCache.getAuthReqParams().getClientId() = " + proAuthoriseCache.getAuthReqParams().getClientId() + ", credentialsDto.getClientId() = " + credentialsDto.getId()
      );
      // todo: throw new Error('invalid client id');
    }

    // todo: check PKCE values by a private function
    // cf. https://auth0.com/docs/authorization/flows/authorization-code-flow-with-proof-key-for-code-exchange-pkce
    if (proAuthoriseCache.getAuthReqParams().getCodeChallenge() == null) {
      this.logger.log("AuthorisationCodeFlowDomainSerivice", "invalid codeChallenge");
      // todo: throw new Error('invalid codeChallenge');
    }
    String codeChallengeMethod = proAuthoriseCache.getAuthReqParams().getCodeChallengeMethod();
    if (!codeChallengeMethod.equals("plain") && !codeChallengeMethod.equals("SHA256")) {
      this.logger.log(
          "AuthorisationCodeFlowDomainSerivice",
          "invalid codeChallengeMethod: codeChallengeMethod = " + codeChallengeMethod);
      // todo: throw new Error('invalid codeChallengeMethod');
    }
    // todo: use `SHA256` only
    //    String codeChallenge = codeChallengeMethod.equals("SHA256") ? base64url.fromBase64(crypto.createHash('sha256').update(requestDto.getCodeVerifier()).digest('base64')) : requestDto.getCodeVerifier();
    String codeChallenge = requestDto.getCodeVerifier();
    if (!proAuthoriseCache.getAuthReqParams().getCodeChallenge().equals(codeChallenge)) {
      // todo: throw new Error(`codeChallenge is expected to be ${codeChallenge}, but ${proAuthoriseCache.authReqParams.codeChallenge} is given`);
      this.logger.log(
          "AuthorisationCodeFlowDomainSerivice",
          "codeChallenge = " + codeChallenge + ", proAuthoriseCache.getAuthReqParams().getCodeChallenge() = " + proAuthoriseCache.getAuthReqParams().getCodeChallenge()
      );
    }

    return new TokenValidationResultDto(credentialsDto.getId(), proAuthoriseCache);
  }

  /**
   * generate tokens
   * @param tokenValidationResultDto
   * @return GenerateTokensResponseDto
   */
  @Override
  public GenerateTokensResponseDto generateTokens(TokenValidationResultDto<ProAuthoriseCache> tokenValidationResultDto) {

    Usr usr = this.usrLogic.getUsrBySub(tokenValidationResultDto.getPayload().getSub());
    if(usr == null) {
      // todo: error handling
    }

    this.logger.log("AuthorisationCodeFlowDomainSerivice", "tokenValidationResultDto.payload = " + tokenValidationResultDto.getPayload().toString());

    try {
      GenerateTokensResponseDto responseDto = this.tokenLogic.generateTokens(
          tokenValidationResultDto.getClientId(), usr.getSub(),
          tokenValidationResultDto.getPayload().getScopeRequested(),true, null
      );
      return responseDto;

    } catch (Exception e) {
      // todo: error handling
      e.printStackTrace();
      return null;
    }
  }
}
