package tokyomap.oauth.domain.services.token;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.RefreshToken;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.logics.UsrLogic;
import tokyomap.oauth.domain.services.common.TokenScrutinyFailureException;
import tokyomap.oauth.domain.services.common.TokenScrutiny;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

@Service
public class RefreshTokenService extends TokenService<SignedJWT> {

  private final TokenScrutiny tokenScrutiny;
  private final TokenLogic tokenLogic;
  private final UsrLogic usrLogic;
  private final Logger logger;
  private final Decorder decorder;

  @Autowired
  public RefreshTokenService(
      TokenScrutiny tokenScrutiny, ClientLogic clientLogic, Decorder decorder,
      TokenLogic tokenLogic, UsrLogic usrLogic, Logger logger
  ) {
    super(clientLogic, decorder, logger);
    this.tokenScrutiny = tokenScrutiny;
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
  public TokenValidationResultDto<SignedJWT> execValidation(GenerateTokensRequestDto requestDto, String authorization) {

    CredentialsDto credentialsDto = this.validateClient(requestDto, authorization);

    String incomingToken = requestDto.getRefreshToken();

    SignedJWT refreshJWT = this.tokenScrutiny.execute(credentialsDto, incomingToken);

    RefreshToken refreshToken = this.tokenLogic.getRefreshToken(incomingToken);
    if(refreshToken == null) {
      this.logger.log(RefreshTokenService.class.getName(), "refreshToken is null");
      throw new TokenScrutinyFailureException("no matching refreshToken");
    }

    return new TokenValidationResultDto(credentialsDto.getId(), refreshJWT);
  }

  /**
   * generate tokens to refresh old ones
   * @param tokenValidationResultDto
   * @return GenerateTokensResponseDto
   */
  @Override
  @Transactional
  public GenerateTokensResponseDto execute(TokenValidationResultDto<SignedJWT> tokenValidationResultDto) {

    try {
      Usr usr = this.usrLogic.getUsrBySub(tokenValidationResultDto.getPayload().getJWTClaimsSet().getSubject());
      if(usr == null) {
        throw new TokenScrutinyFailureException("no matching usr");
      }

      String clientId = tokenValidationResultDto.getPayload().getJWTClaimsSet().getStringClaim("clientId");
      String[] scopes = tokenValidationResultDto.getPayload().getJWTClaimsSet().getStringArrayClaim("scopes");
      return this.tokenLogic.generateTokens(clientId, usr.getSub(), scopes, true, null);

    } catch (ParseException e) {
      throw new TokenScrutinyFailureException(e.getMessage());

    } catch (Exception e) {
      throw new TokenScrutinyFailureException(e.getMessage());
    }
  }
}
