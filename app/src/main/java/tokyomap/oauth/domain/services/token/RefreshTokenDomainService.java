package tokyomap.oauth.domain.services.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

@Component
public class RefreshTokenDomainService extends TokenDomainService<TokenPayloadDto> {

  private final TokenLogic tokenLogic;
  private final UsrLogic usrLogic;
  private final Logger logger;

  @Autowired
  public RefreshTokenDomainService(ClientLogic clientLogic, Decorder decorder, TokenLogic tokenLogic, UsrLogic usrLogic, Logger logger) {
    super(clientLogic, decorder);
    this.tokenLogic = tokenLogic;
    this.usrLogic = usrLogic;
    this.logger = logger;
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
    this.logger.log("RefreshTokenDomainService", "refreshToken is null");
    // todo: throw new Error(`${util.fetchCurrentDatetimeJst()} [refreshTokenLogic.execValidation] no matching refreshToken`);
  }

    String[] refreshTokenSplit = refreshToken.getRefreshToken().split("\\.");

    // todo: create a dto from a string

    try {
      String payload = new String((new Base64()).decode(refreshTokenSplit[1].getBytes()));
      TokenPayloadDto tokenPayloadDto = new ObjectMapper().readValue(payload, TokenPayloadDto.class);

      // todo:
//    if (!tokenPayloadDto.getIss().equals(config.authServer.host)) {
//      throw new Error(`[refreshTokenLogic.execValidation] payload.iss is expected to be ${config.authServer.host}, but ${payload.iss}`);
//    }
//    if ((!Array.isArray(tokenPayloadDto.getAud()) || !tokenPayloadDto.getAud().includes(client.clientId)) && !tokenPayloadDto.getAud().equals(client.clientId)) {
//      throw new Error(`[refreshTokenLogic.execValidation] payload.aud is invalid`);
//    }
//    LocalDateTime now = LocalDateTime.now();
//    if (payload.exp < now || now < payload.iat) {
//      throw new Error('[refreshTokenLogic.execValidation] payload.exp is invalid');
//    }
//    if (payload.clientId !== clientId) {
//      throw new Error(`${util.fetchCurrentDatetimeJst()} [refreshTokenLogic.execValidation] invalid clientId: payload.cliendId = ${payload.clientId}, actual clientId = ${clientId}`);
//    }

      return new TokenValidationResultDto(credentialsDto.getId(), tokenPayloadDto);

    } catch (Exception e) {
      // todo: handle exception properly
      e.printStackTrace();
      return null;
    }
  }

  /**
   * generate tokens to refresh old ones
   * @param tokenValidationResultDto
   * @return GenerateTokensResponseDto
   */
  @Override
  public GenerateTokensResponseDto generateTokens(TokenValidationResultDto<TokenPayloadDto> tokenValidationResultDto) {

    Usr usr = this.usrLogic.getUsrBySub(tokenValidationResultDto.getPayload().getSub());
    if(usr == null) {
      // todo: error handling
    }

    try {
      return this.tokenLogic.generateTokens(
          tokenValidationResultDto.getPayload().getClientId(), usr.getSub(), tokenValidationResultDto.getPayload().getScope(),true, null
      );

    } catch (Exception e) {
      // todo: error handling
      e.printStackTrace();
      return null;
    }
  }
}
