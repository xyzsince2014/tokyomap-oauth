package tokyomap.oauth.application.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tokyomap.oauth.domain.entities.redis.ProAuthoriseCache;
import tokyomap.oauth.domain.services.token.AuthorisationCodeFlowSerivice;
import tokyomap.oauth.domain.services.token.ClientCredentialsSerivce;
import tokyomap.oauth.domain.services.token.RefreshTokenService;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenPayloadDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;

@RestController
@RequestMapping("/token")
public class TokenRestController {

  private final AuthorisationCodeFlowSerivice authorisationCodeFlowSerivce;
  private final RefreshTokenService refreshTokenService;
  private final ClientCredentialsSerivce clientCredentialsSerivce;

  @Autowired
  public TokenRestController(
      AuthorisationCodeFlowSerivice authorisationCodeFlowSerivce,
      RefreshTokenService refreshTokenService,
      ClientCredentialsSerivce clientCredentialsSerivce
  ) {
    this.authorisationCodeFlowSerivce = authorisationCodeFlowSerivce;
    this.refreshTokenService = refreshTokenService;
    this.clientCredentialsSerivce = clientCredentialsSerivce;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public GenerateTokensResponseDto generateTokens(GenerateTokensRequestDto requestDto, @RequestHeader("Authorization") String authorization) {
    switch (requestDto.getGrantType()) {
      case "AUTHORISATION_CODE": { // todo: use a Constant
        TokenValidationResultDto<ProAuthoriseCache> tokenValidationResultDto = this.authorisationCodeFlowSerivce.execValidation(requestDto, authorization);
        GenerateTokensResponseDto response = this.authorisationCodeFlowSerivce.generateTokens(tokenValidationResultDto);
        return response;
      }
      case "REFRESH_TOKEN": { // todo: use a Constant
        TokenValidationResultDto<TokenPayloadDto> tokenValidationResultDto = this.refreshTokenService.execValidation(requestDto, authorization);
        GenerateTokensResponseDto response = this.refreshTokenService.generateTokens(tokenValidationResultDto);
        return response;
      }
      case "CLIENT_CREDENTIALS": { // todo: use a Constant
        TokenValidationResultDto<CredentialsDto> tokenValidationResultDto = this.clientCredentialsSerivce.execValidation(requestDto, authorization);
        GenerateTokensResponseDto response = this.clientCredentialsSerivce.generateTokens(tokenValidationResultDto);
        return response;
      }
      default: {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
    }
  }
}
