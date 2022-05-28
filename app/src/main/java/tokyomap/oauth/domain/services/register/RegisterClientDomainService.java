package tokyomap.oauth.domain.services.register;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.dtos.RequestClientDto;
import tokyomap.oauth.dtos.ClientValidationResultDto;
import tokyomap.oauth.utils.Logger;

@Service
public class RegisterClientDomainService {

  // todo: define in a config file
  private static final String[] TOKEN_ENDPOINT_AUTH_METHODS = new String[] {
      "NONE", // the client does not authenticate to the token endpoint, either because it doesn’t use the token endpoint, or it uses the token endpoint but is a public client
      "CLIENT_SECRET_BASIC", // the client sends its client secret using HTTP Basic
      "CLIENT_SECRET_POST", // the client sends its client secret using HTTP form parameters
      "CLIENT_SECRET_JWT", // the client will create a JWT symmetrically signed with its client secret
      "PRIVATE_KEY_JWT" // the client will create a JWT asymmetrically signed with its private key. The public key will need to be registered with the authorisation server
  };
  private static final String REGISTRATION_ENDPOINT = "http://localhost:80/register";

  private final ClientLogic clientLogic;
  private final Logger logger;

  @Autowired
  public RegisterClientDomainService(ClientLogic clientLogic, Logger logger) {
    this.clientLogic = clientLogic;
    this.logger = logger;
  }

  public ClientValidationResultDto execValidation(RequestClientDto requestClientDto) {

    // make sure that the client has registered at least one redirect URI
    if (requestClientDto.getRedirectUris() == null || requestClientDto.getRedirectUris().length == 0) {
      this.logger.log("RegisterClientDomainService", "invalid redirectUris");
      // todo: throw new Error(`[registerClientLogic.execValidation] invalid redirectUris`);
    }

    String tokenEndpointAuthMethod = requestClientDto.getTokenEndpointAuthMethod() != null ? requestClientDto.getTokenEndpointAuthMethod() : "CLIENT_SECRET_BASIC";

    if (!Arrays.stream(TOKEN_ENDPOINT_AUTH_METHODS).anyMatch(authMethod -> authMethod.equals(requestClientDto.getTokenEndpointAuthMethod()))) {
      this.logger.log("RegisterClientDomainService", "invalid tokenEndpointAuthMethod");
      // todo: throw new Error(`[registerService] validateClientMetadata invalid tokenEndpointAuthMethod ${tokenEndpointAuthMethod}`);
    }

    // if the client specify neither the grantTypes nor responseTypes, we default them to the authorisation code grant
    if(requestClientDto.getGrantTypes() == null && requestClientDto.getResponseTypes() == null) {
      this.logger.log("RegisterClientDomainService", "requestClientDto.getGrantTypes() == null && requestClientDto.getResponseTypes() == null");
      return new ClientValidationResultDto(new String[] {"AUTHORISATION_CODE"}, new String[] {"CODE"}, tokenEndpointAuthMethod);
    }

    // if the client requests with specified grantTypes but not corresponding responseTypes or vice versa, we fill in the missing value for them
    if (requestClientDto.getGrantTypes() == null) {
      this.logger.log("RegisterClientDomainService", "requestClientDto.getGrantTypes() == null");
      String[] grantTypes = (Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals("CODE"))) ? new String[] {"AUTHORISATION_CODE"} : new String[] {};
      return new ClientValidationResultDto(grantTypes, requestClientDto.getResponseTypes(), tokenEndpointAuthMethod);
    }
    if (requestClientDto.getResponseTypes() == null) {
      this.logger.log("RegisterClientDomainService", "requestClientDto.getResponseTypes() == null");
      String[] responseTypes = (Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals("AUTHORISATION_CODE"))) ? new String[] {"CODE"} : new String[] {};
      return new ClientValidationResultDto(requestClientDto.getGrantTypes(), responseTypes, tokenEndpointAuthMethod);
    }

    ClientValidationResultDto resultDto = new ClientValidationResultDto(requestClientDto.getGrantTypes(), requestClientDto.getResponseTypes(), tokenEndpointAuthMethod);

    if (Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals("AUTHORISATION_CODE"))
        && !Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals("CODE"))
    ) {
      this.logger.log("RegisterClientDomainService", "Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals(\"AUTHORISATION_CODE\")) && !Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals(\"CODE\"))");
      List<String> responseTypeList = Arrays.asList(resultDto.getResponseTypes());
      responseTypeList.add("CODE");
      String[] responseTypes = new String[responseTypeList.size()];
      resultDto.setResponseTypes(responseTypeList.toArray(responseTypes));
      return resultDto;
    }

    if (!Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals("AUTHORISATION_CODE"))
        && Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals("CODE"))
    ) {
      this.logger.log("RegisterClientDomainService", "!Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals(\"AUTHORISATION_CODE\")) && Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals(\"CODE\"))");
      List<String> grantTypeList = Arrays.asList(resultDto.getGrantTypes());
      grantTypeList.add("AUTHORISATION_CODE");
      String[] grantTypes = new String[grantTypeList.size()];
      resultDto.setGrantTypes(grantTypeList.toArray(grantTypes));
      return resultDto;
    }

    // todo:
//    if (Arrays.stream(resultDto.getGrantTypes()).filter(grantType -> !config.grantTypes.includes(grantType)).length > 0) {
//      throw new Error(`[registerService] validateClientMetadata invalid grantTypes ${grantTypes}`);
//    }
//    if (Arrays.stream(resultDto.getResponseTypes()).filter(responseType -> !config.responseTypes.includes(responseType)).length > 0) {
//      throw new Error(`[registerService] validateClientMetadata invalid responseTypes ${responseTypes}`);
//    }

    return resultDto;
  }

  public Client register(RequestClientDto requestClientDto, ClientValidationResultDto resultDto) {

    String clientId = RandomStringUtils.random(8, true, true);
    String clientSecret = Arrays.stream(TOKEN_ENDPOINT_AUTH_METHODS).anyMatch(authMethod -> authMethod.equals(resultDto.getTokenEndpointAuthMethod())) ? RandomStringUtils.random(8, true, true) : null;
    String registrationAccessToken = RandomStringUtils.random(8, true, true);
    String registrationClientUri = REGISTRATION_ENDPOINT + "/"  + clientId;

    // merge with requestClientDto
    Client client = new Client(
        clientId,
        clientSecret,
        requestClientDto.getClientName(),
        resultDto.getTokenEndpointAuthMethod() != null ? resultDto.getTokenEndpointAuthMethod() : requestClientDto.getTokenEndpointAuthMethod(),
        requestClientDto.getClientUri(),
        String.join(" ", requestClientDto.getRedirectUris()),
        resultDto.getGrantTypes() != null ? String.join(" ", resultDto.getGrantTypes()) : String.join(" ", requestClientDto.getGrantTypes()),
        resultDto.getResponseTypes() != null ? String.join(" ", resultDto.getResponseTypes()) : String.join(" ", requestClientDto.getResponseTypes()),
        String.join(" ", requestClientDto.getScope()),
        registrationAccessToken,
        registrationClientUri
    );

    // client should be the form of
    // {
    //   "clientName":"clientWebApp",
    //   "clientUri":"http://localhost:9000/",
    //   "redirectUris":"http://localhost:9000/callback http://localhost:9000/dummy",
    //   "grantTypes":"AUTHORISATION_CODE REFRESH_TOKEN PASSWORD CLIENT_CREDENTIALS",
    //   "responseTypes": "CODE TOKEN"],
    //   "tokenEndpointAuthMethod":"CLIENT_SECRET_BASIC",
    //   "scope":"read write delete openid profile email address phone",
    //   "clientId":"DbdU6xXG1ZnB1ZIkeFkmS84GmEUp3OWZ",
    //   "clientSecret":"xh34OiNv2ZQ4VMVXrdrXu85xKM5mYXiT",
    //   "registrationAccessToken":"OkxhTZTFJGOP0R3XgT1iOHAnsqi67nMI",
    //   "registrationClientUri":"http://localhost:9001/register/DbdU6xXG1ZnB1ZIkeFkmS84GmEUp3OWZ"
    //  }
    Client clientRegistered = this.clientLogic.registerClient(client);

    this.logger.log("RegisterClientDomainService", "clientRegistered = " + clientRegistered.toString());

    return clientRegistered;
  }
}
