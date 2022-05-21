package tokyomap.oauth.domain.services.token;

import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.entities.redis.AuthCache;
import tokyomap.oauth.domain.logics.AuthCodeLogic;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.logics.UsrLogic;
import tokyomap.oauth.dtos.userinfo.token.ClientCredentialsDto;
import tokyomap.oauth.dtos.userinfo.token.IssueTokensRequestDto;
import tokyomap.oauth.dtos.userinfo.token.IssueTokensResponseDto;
import tokyomap.oauth.dtos.userinfo.token.ValidationResultDto;
import tokyomap.oauth.utils.Logger;

@Component
public class TokenDomainService {

  private final AuthCodeLogic authCodeLogic;
  private final ClientLogic clientLogic;
  private final TokenLogic tokenLogic;
  private final UsrLogic usrLogic;
  private final Logger logger;

  @Autowired
  public TokenDomainService(AuthCodeLogic authCodeLogic, ClientLogic clientLogic, TokenLogic tokenLogic, UsrLogic usrLogic, Logger logger) {
    this.authCodeLogic = authCodeLogic;
    this.clientLogic = clientLogic;
    this.tokenLogic = tokenLogic;
    this.usrLogic = usrLogic;
    this.logger = logger;
  }

  /**
   * execute validation of request to the token endpoint
   * @return ValidationResultDto
   */
  public ValidationResultDto execValidation(IssueTokensRequestDto requestDto, String authorization) {

    ClientCredentialsDto clientCredentialsDto = this.validateClient(requestDto, authorization);
    AuthCache authCache = this.authCodeLogic.getCacheByCode(requestDto.getCode());

    //  check the expiry date of the auth code here
    if (!clientCredentialsDto.getClientId().equals(authCache.getAuthReqParams().getClientId())) {
      this.logger.log(
          "TokenDomainService",
          "invalid client id: authCache.getAuthReqParams().getClientId() = " + authCache.getAuthReqParams().getClientId() + ", clientCredentialsDto.getClientId() = " + clientCredentialsDto.getClientId()
      );
      // todo: throw new Error('invalid client id');
    }

    // todo: check PKCE values by a private function
    // cf. https://auth0.com/docs/authorization/flows/authorization-code-flow-with-proof-key-for-code-exchange-pkce
    if (authCache.getAuthReqParams().getCodeChallenge() == null) {
      this.logger.log("TokenDomainService", "invalid codeChallenge");
      // todo: throw new Error('invalid codeChallenge');
    }
    String codeChallengeMethod = authCache.getAuthReqParams().getCodeChallengeMethod();
    if (!codeChallengeMethod.equals("plain") && !codeChallengeMethod.equals("SHA256")) {
      this.logger.log(
          "TokenDomainService",
          "invalid codeChallengeMethod: codeChallengeMethod = " + codeChallengeMethod);
      // todo: throw new Error('invalid codeChallengeMethod');
    }
    // todo: use `SHA256` only
    //    String codeChallenge = codeChallengeMethod.equals("SHA256") ? base64url.fromBase64(crypto.createHash('sha256').update(requestDto.getCodeVerifier()).digest('base64')) : requestDto.getCodeVerifier();
    String codeChallenge = requestDto.getCodeVerifier();
    if (!authCache.getAuthReqParams().getCodeChallenge().equals(codeChallenge)) {
      // todo: throw new Error(`codeChallenge is expected to be ${codeChallenge}, but ${authCache.authReqParams.codeChallenge} is given`);
      this.logger.log(
          "TokenDomainService",
          "codeChallenge = " + codeChallenge + ", authCache.getAuthReqParams().getCodeChallenge() = " + authCache.getAuthReqParams().getCodeChallenge()
      );
    }

    return new ValidationResultDto(clientCredentialsDto.getClientId(), authCache);
  }

  /**
   * validate client
   * @param requestDto
   * @param authorization
   * @return
   */
  private ClientCredentialsDto validateClient(IssueTokensRequestDto requestDto, String authorization) {

    String clientId = "";
    String clientSecret = "";

    // fetch clientId & clientSecret from the authorization header or the post params, then check them
    ClientCredentialsDto clientCredentials = this.decodeClientCredentials(authorization);

    clientId = clientCredentials.getClientId();
    clientSecret = clientCredentials.getClientSecret();

    if (requestDto.getClientId() != null) {
      if (clientCredentials.getClientId() != null) {
        // return an error if we've already seen the client's credentials in the authorization header
        // todo: console.log(`${util.fetchCurrentDatetimeJst()} [clientLogic.validateClient] invalid clientId`);
        // todo: throw new Error('invalid clientId');
      }
      clientId = requestDto.getClientId();
      clientSecret = requestDto.getClientSecret();
    }

    Client client = this.clientLogic.getClientByClientId(clientId);

    // todo: validate client
    //  if (client == null || client.get().getClientSecret() != clientSecret) {
    //    throw new Error('invalid client');
    //  }

    // todo: malfunctioning, fix
    //  String[] clientScope = client.getScope().split(" ");
    String[] dummyScope = new String[] {"read", "write", "delete", "openid", "profile", "email", "address", "phone"};

    return new ClientCredentialsDto(clientId, clientSecret, dummyScope);
  }

  /**
   * decode an authorization header to client credentials
   * @param authorization
   * @return ClientCredentials
   */
  private ClientCredentialsDto decodeClientCredentials(String authorization) {
    if(authorization == null) {
      return new ClientCredentialsDto();
    }

    Base64 base64 = new Base64();
    String encodedClientCredentials = authorization.substring("Basic ".length());
    String decodedString = new String(base64.decode(encodedClientCredentials.getBytes()));
    String[] splitString = decodedString.split(":");

    ClientCredentialsDto clientCredentials = new ClientCredentialsDto(splitString[0], splitString[1]);

    return clientCredentials;
  };

  /**
   * issue tokens
   * @param validationResultDto
   * @return IssueTokensResponseDto
   */
  public IssueTokensResponseDto issueTokens(ValidationResultDto validationResultDto) {

    Optional<Usr> optionalUsr = this.usrLogic.getUsrBySub(validationResultDto.getAuthCache().getSub());
    if(optionalUsr == null) {
      // todo: error handling
    }

    try {
      IssueTokensResponseDto responseDto = this.tokenLogic.generateTokens(
          validationResultDto.getClientId(), optionalUsr.get().getSub(),
          validationResultDto.getAuthCache().getScopeRequested(),true, null
      );

      return responseDto;
    } catch (Exception e) {
      // todo: error handling
      e.printStackTrace();
      return null;
    }
  }
}
