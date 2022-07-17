package tokyomap.oauth.application.token;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.domain.entities.redis.ProAuthoriseCache;
import tokyomap.oauth.domain.services.token.AuthorisationCodeFlowSerivice;
import tokyomap.oauth.domain.services.token.ClientCredentialsSerivce;
import tokyomap.oauth.domain.services.token.InvalidTokenRequestException;
import tokyomap.oauth.domain.services.token.RefreshTokenService;
import tokyomap.oauth.dtos.ApiResponseDto;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;

@RestController
@RequestMapping("/token")
public class TokenRestController {

  // todo: use constants
  private static final int ERROR_CODE_INVALID_REQUEST = 9000;
  private static final int ERROR_CODE_SERVER_ERROR = 9001;
  private static final String ERROR_MESSAGE_SERVER_ERROR = "Server Error";

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

  @RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded;charset=utf-8")
  public ApiResponseDto generateTokens(GenerateTokensRequestDto requestDto, @RequestHeader("Authorization") String authorization) {

    try {
      switch (requestDto.getGrantType()) {
        case "AUTHORISATION_CODE": { // todo: use a Constant
          TokenValidationResultDto<ProAuthoriseCache> tokenValidationResultDto = this.authorisationCodeFlowSerivce.execValidation(requestDto, authorization);
          GenerateTokensResponseDto responseDto = this.authorisationCodeFlowSerivce.execute(tokenValidationResultDto);
          return responseDto;
        }
        case "REFRESH_TOKEN": { // todo: use a Constant
          TokenValidationResultDto<SignedJWT> tokenValidationResultDto = this.refreshTokenService.execValidation(requestDto, authorization);
          GenerateTokensResponseDto responseDto = this.refreshTokenService.execute(tokenValidationResultDto);
          return responseDto;
        }
        case "CLIENT_CREDENTIALS": { // todo: use a Constant
          TokenValidationResultDto<CredentialsDto> tokenValidationResultDto = this.clientCredentialsSerivce.execValidation(requestDto, authorization);
          GenerateTokensResponseDto responseDto = this.clientCredentialsSerivce.execute(tokenValidationResultDto);
          return responseDto;
        }
        default: {
          throw new InvalidTokenRequestException("Invalid Response Type");
        }
      }

    } catch (InvalidTokenRequestException e) {
      // todo: return with Http Status Code = 400
      return new ApiResponseDto(ERROR_CODE_INVALID_REQUEST, e.getErrorMessage());

    } catch (Exception e) {
      // todo: return with Http Status Code = 400
      return new ApiResponseDto(ERROR_CODE_SERVER_ERROR, ERROR_MESSAGE_SERVER_ERROR);
    }
  }
}
