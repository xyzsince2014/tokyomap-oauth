package tokyomap.oauth.domain.services.register;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import tokyomap.oauth.domain.services.api.v1.ApiException;
import tokyomap.oauth.dtos.ClientValidationResultDto;
import tokyomap.oauth.dtos.RequestClientDto;

public abstract class RegisterService {

  // todo: define in a config file
  protected final String[] TOKEN_ENDPOINT_AUTH_METHODS = new String[] {
      "NONE", // the client does not authenticate to the token endpoint, either because it doesn’t use the token endpoint, or it uses the token endpoint but is a public client
      "CLIENT_SECRET_BASIC", // the client sends its client secret using HTTP Basic
      "CLIENT_SECRET_POST", // the client sends its client secret using HTTP form parameters
      "CLIENT_SECRET_JWT", // the client will create a JWT symmetrically signed with its client secret
      "PRIVATE_KEY_JWT" // the client will create a JWT asymmetrically signed with its private key. The public key will need to be registered with the authorisation server
  };

  protected final String REGISTRATION_ENDPOINT = "http://localhost:80/register";

  protected final String[] GRANT_TYPES = new String[] {
      "AUTHORISATION_CODE", // the authorisation code grant, where the client sends the resource owner to the authorisation endpoint to obtain an authorisation code
      "IMPLICIT", // the implicit grant, where the client sends the resource owner to the authorisation endpoint to obtain a token directly
      "PASSWORD", // the client prompts the resource owner for their username and password and exchanges them for a token at the token endpoint
      "REFRESH_TOKEN", // the refresh token grant, where the client uses a refresh token to obtain a new access token when the resource owner is no longer present
      "CLIENT_CREDENTIALS" // the client credentials grant, where the client uses its own credentials to obtain a token for itself
  };

  protected final String[] RESPONSE_TYPES = new String[] {
      "CODE", // the authorisation code response type, which returns an authorisation code to be handed in at the token endpoint to get a token
      "TOKEN" // the implicit response type, which returns a token directly to the redirect URI
  };

  /**
   * execute validation
   * @param requestClientDto
   * @return ClientValidationResultDto
   * @throws ApiException
   */
  public ClientValidationResultDto execValidation(RequestClientDto requestClientDto) throws ApiException {

    // make sure that the client has registered at least one redirect URI
    if (requestClientDto.getRedirectUris() == null || requestClientDto.getRedirectUris().length == 0) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Redirect Uris.");
    }

    String tokenEndpointAuthMethod = requestClientDto.getTokenEndpointAuthMethod() != null ? requestClientDto.getTokenEndpointAuthMethod() : "CLIENT_SECRET_BASIC";

    if (!Arrays.stream(TOKEN_ENDPOINT_AUTH_METHODS).anyMatch(authMethod -> authMethod.equals(requestClientDto.getTokenEndpointAuthMethod()))) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid tokenEndpointAuthMethod.");
    }

    // if the client specify neither the grantTypes nor responseTypes, default them to the authorisation code grant
    if(requestClientDto.getGrantTypes() == null && requestClientDto.getResponseTypes() == null) {
      return new ClientValidationResultDto(new String[] {"AUTHORISATION_CODE"}, new String[] {"CODE"}, tokenEndpointAuthMethod);
    }

    // if the client requests with specified grantTypes but not corresponding responseTypes or vice versa, we fill in the missing value for them
    if (requestClientDto.getGrantTypes() == null) {
      String[] grantTypes = (Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals("CODE"))) ? new String[] {"AUTHORISATION_CODE"} : new String[] {};
      return new ClientValidationResultDto(grantTypes, requestClientDto.getResponseTypes(), tokenEndpointAuthMethod);
    }
    if (requestClientDto.getResponseTypes() == null) {
      String[] responseTypes = (Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals("AUTHORISATION_CODE"))) ? new String[] {"CODE"} : new String[] {};
      return new ClientValidationResultDto(requestClientDto.getGrantTypes(), responseTypes, tokenEndpointAuthMethod);
    }

    ClientValidationResultDto validationResultDto = new ClientValidationResultDto(requestClientDto.getGrantTypes(), requestClientDto.getResponseTypes(), tokenEndpointAuthMethod);

    if (Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals("AUTHORISATION_CODE"))
        && !Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals("CODE"))
    ) {
      List<String> responseTypeList = Arrays.asList(validationResultDto.getResponseTypes());
      responseTypeList.add("CODE");
      String[] responseTypes = new String[responseTypeList.size()];
      validationResultDto.setResponseTypes(responseTypeList.toArray(responseTypes));
      return validationResultDto;
    }

    if (!Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals("AUTHORISATION_CODE"))
        && Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals("CODE"))
    ) {
      List<String> grantTypeList = Arrays.asList(validationResultDto.getGrantTypes());
      grantTypeList.add("AUTHORISATION_CODE");
      String[] grantTypes = new String[grantTypeList.size()];
      validationResultDto.setGrantTypes(grantTypeList.toArray(grantTypes));
      return validationResultDto;
    }

    // throw Exception if either grantTypes or responseTypes has an invalid type
    if(!Arrays.asList(GRANT_TYPES).containsAll(Arrays.asList(validationResultDto.getGrantTypes()))) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Grant Types");
    }
    if(!Arrays.asList(RESPONSE_TYPES).containsAll(Arrays.asList(validationResultDto.getResponseTypes()))) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Response Types");
    }

    return validationResultDto;
  }
}
