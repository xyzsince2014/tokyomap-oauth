package tokyomap.oauth.domain.services.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.RefreshToken;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.logics.UsrLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenPayloadDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

@Service
public class RefreshTokenService extends TokenService<TokenPayloadDto> {

  // todo: define in a config file
  private static final String AUTH_SERVER_HOST = "http://localhost:80";

  // todo: `private static final String[] AUDIENCE = new String[] {"http://localhost:9002"};`
  private static final String AUDIENCE = "http://localhost:9002"; // registered resource servers

  private final TokenLogic tokenLogic;
  private final UsrLogic usrLogic;
  private final Logger logger;
  private final Decorder decorder;

  @Autowired
  public RefreshTokenService(ClientLogic clientLogic, Decorder decorder, TokenLogic tokenLogic, UsrLogic usrLogic, Logger logger) {
    super(clientLogic, decorder, logger);
    this.tokenLogic = tokenLogic;
    this.usrLogic = usrLogic;
    this.logger = logger;
    this.decorder = decorder;
  }

  /**
   * execute validation of request to the token endpoint with a refresh token
   * @return TokenValidationResultDto
   */
  @Override
  public TokenValidationResultDto<TokenPayloadDto> execValidation(GenerateTokensRequestDto requestDto, String authorization) {

    CredentialsDto credentialsDto = this.validateClient(requestDto, authorization);

    String incomingToken = requestDto.getRefreshToken();

    // todo:
    //    if (!jose.jws.JWS.verify(incomingToken, jose.KEYUTIL.getKey(config.rsaPublicKey), [config.rsaPublicKey.alg])) {
    //      throw new Error(`${util.fetchCurrentDatetimeJst()} [refreshTokenLogic.execValidation] incoming token invalid`);
    //    }

    RefreshToken refreshToken = this.tokenLogic.getRefreshToken(incomingToken);

    if(refreshToken == null) {
      this.logger.log("RefreshTokenService", "refreshToken is null");
      throw new InvalidTokenRequestException("no matching refreshToken");
    }

    String[] refreshTokenSplit = refreshToken.getRefreshToken().split("\\.");

    try {
      String payload = this.decorder.decodeBase64String(refreshTokenSplit[1]);
      this.logger.log(RefreshTokenService.class.getName(), "payload = " + payload);

      TokenPayloadDto tokenPayloadDto = new ObjectMapper().registerModule(new JavaTimeModule()).readValue(payload, TokenPayloadDto.class);

      if (!tokenPayloadDto.getIss().equals(AUTH_SERVER_HOST)) {
        throw new InvalidTokenRequestException("invalid tokenPayloadDto.iss: payloadtokenPayloadDto.getIss() = " + tokenPayloadDto.getIss() + ", AUTH_SERVER_HOST " + AUTH_SERVER_HOST);
      }

      // todo: validation here is necessary ?
      if (tokenPayloadDto.getAud() == null || !tokenPayloadDto.getAud().equals(AUDIENCE)) {
        throw new InvalidTokenRequestException("tokenPayloadDto.aud is invalid");
      }

      LocalDateTime now = LocalDateTime.now();
      if (now.isBefore(tokenPayloadDto.getIat()) || now.isAfter(tokenPayloadDto.getExp())) {
        this.logger.log(
            RefreshTokenService.class.getName(),
            "tokenPayloadDto.iap or tokenPayloadDto.exp is invalid: tokenPayloadDto.iat = " + tokenPayloadDto.getIat().toString() + ", tokenPayloadDto.exp = " + tokenPayloadDto.getExp().toString()
        );
        throw new InvalidTokenRequestException("tokenPayloadDto.iat or tokenPayloadDto.exp is invalid");
      }

      if (!tokenPayloadDto.getClientId().equals(credentialsDto.getId())) {
        this.logger.log(
            RefreshTokenService.class.getName(),
            "tokenPayloadDto.clientId is invalid: tokenPayloadDto.getClientId() = " + tokenPayloadDto.getClientId()
        );
        throw new InvalidTokenRequestException("tokenPayloadDto.clientId is invalid");
      }

      return new TokenValidationResultDto(credentialsDto.getId(), tokenPayloadDto);

    } catch (Exception e) {
      throw new InvalidTokenRequestException(e.getMessage());
    }
  }

  /**
   * generate tokens to refresh old ones
   * @param tokenValidationResultDto
   * @return GenerateTokensResponseDto
   */
  @Override
  @Transactional
  public GenerateTokensResponseDto generateTokens(TokenValidationResultDto<TokenPayloadDto> tokenValidationResultDto) {

    Usr usr = this.usrLogic.getUsrBySub(tokenValidationResultDto.getPayload().getSub());
    if(usr == null) {
      throw new InvalidTokenRequestException("no matching usr");
    }

    try {
      return this.tokenLogic.generateTokens(
          tokenValidationResultDto.getPayload().getClientId(),
          usr.getSub(),
          tokenValidationResultDto.getPayload().getScopes(),
          true,
          null
      );

    } catch (Exception e) {
      throw new InvalidTokenRequestException(e.getMessage());
    }
  }
}
