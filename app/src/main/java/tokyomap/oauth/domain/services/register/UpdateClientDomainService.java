package tokyomap.oauth.domain.services.register;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.dtos.ClientValidationResultDto;
import tokyomap.oauth.dtos.RequestClientDto;
import tokyomap.oauth.dtos.ResponseClientDto;
import tokyomap.oauth.utils.Logger;

@Service
public class UpdateClientDomainService {

  // todo: define in a config file
  private static final String[] TOKEN_ENDPOINT_AUTH_METHODS = new String[] {
      "NONE", // the client does not authenticate to the token endpoint, either because it doesn’t use the token endpoint, or it uses the token endpoint but is a public client
      "CLIENT_SECRET_BASIC", // the client sends its client secret using HTTP Basic
      "CLIENT_SECRET_POST", // the client sends its client secret using HTTP form parameters
      "CLIENT_SECRET_JWT", // the client will create a JWT symmetrically signed with its client secret
      "PRIVATE_KEY_JWT" // the client will create a JWT asymmetrically signed with its private key. The public key will need to be registered with the authorisation server
  };

  private final ClientLogic clientLogic;
  private final Logger logger;

  @Autowired
  public UpdateClientDomainService(ClientLogic clientLogic, Logger logger) {
    this.clientLogic = clientLogic;
    this.logger = logger;
  }

  // todo: merge with RegisterClientService#execValidation(RequestClientDto requestClientDto)
  // todo: rename to validateRequestClientDto(RequestClientDto requestClientDto)
  public ClientValidationResultDto execValidation(RequestClientDto requestClientDto) {

    // make sure that the client has registered at least one redirect URI
    if (requestClientDto.getRedirectUris() == null || requestClientDto.getRedirectUris().length == 0) {
      this.logger.log("RegisterClientService", "invalid redirectUris");
      // todo: throw new Error(`[registerClientLogic.execValidation] invalid redirectUris`);
    }

    String tokenEndpointAuthMethod = requestClientDto.getTokenEndpointAuthMethod() != null ? requestClientDto.getTokenEndpointAuthMethod() : "CLIENT_SECRET_BASIC";

    if (!Arrays.stream(TOKEN_ENDPOINT_AUTH_METHODS).anyMatch(authMethod -> authMethod.equals(requestClientDto.getTokenEndpointAuthMethod()))) {
      this.logger.log("RegisterClientService", "invalid tokenEndpointAuthMethod");
      // todo: throw new Error(`[registerService] validateClientMetadata invalid tokenEndpointAuthMethod ${tokenEndpointAuthMethod}`);
    }

    // if the client specify neither the grantTypes nor responseTypes, we default them to the authorisation code grant
    if(requestClientDto.getGrantTypes() == null && requestClientDto.getResponseTypes() == null) {
      this.logger.log("RegisterClientService", "requestClientDto.getGrantTypes() == null && requestClientDto.getResponseTypes() == null");
      return new ClientValidationResultDto(new String[] {"AUTHORISATION_CODE"}, new String[] {"CODE"}, tokenEndpointAuthMethod);
    }

    // if the client requests with specified grantTypes but not corresponding responseTypes or vice versa, we fill in the missing value for them
    if (requestClientDto.getGrantTypes() == null) {
      this.logger.log("RegisterClientService", "requestClientDto.getGrantTypes() == null");
      String[] grantTypes = (Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals("CODE"))) ? new String[] {"AUTHORISATION_CODE"} : new String[] {};
      return new ClientValidationResultDto(grantTypes, requestClientDto.getResponseTypes(), tokenEndpointAuthMethod);
    }
    if (requestClientDto.getResponseTypes() == null) {
      this.logger.log("RegisterClientService", "requestClientDto.getResponseTypes() == null");
      String[] responseTypes = (Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals("AUTHORISATION_CODE"))) ? new String[] {"CODE"} : new String[] {};
      return new ClientValidationResultDto(requestClientDto.getGrantTypes(), responseTypes, tokenEndpointAuthMethod);
    }

    ClientValidationResultDto validationResultDto = new ClientValidationResultDto(requestClientDto.getGrantTypes(), requestClientDto.getResponseTypes(), tokenEndpointAuthMethod);

    if (Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals("AUTHORISATION_CODE"))
        && !Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals("CODE"))
    ) {
      this.logger.log("RegisterClientService", "Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals(\"AUTHORISATION_CODE\")) && !Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals(\"CODE\"))");
      List<String> responseTypeList = Arrays.asList(validationResultDto.getResponseTypes());
      responseTypeList.add("CODE");
      String[] responseTypes = new String[responseTypeList.size()];
      validationResultDto.setResponseTypes(responseTypeList.toArray(responseTypes));
      return validationResultDto;
    }

    if (!Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals("AUTHORISATION_CODE"))
        && Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals("CODE"))
    ) {
      this.logger.log("RegisterClientService", "!Arrays.stream(requestClientDto.getGrantTypes()).anyMatch(grantType -> grantType.equals(\"AUTHORISATION_CODE\")) && Arrays.stream(requestClientDto.getResponseTypes()).anyMatch(responseType -> responseType.equals(\"CODE\"))");
      List<String> grantTypeList = Arrays.asList(validationResultDto.getGrantTypes());
      grantTypeList.add("AUTHORISATION_CODE");
      String[] grantTypes = new String[grantTypeList.size()];
      validationResultDto.setGrantTypes(grantTypeList.toArray(grantTypes));
      return validationResultDto;
    }

    // todo:
//    if (Arrays.stream(validationResultDto.getGrantTypes()).filter(grantType -> !config.grantTypes.includes(grantType)).length > 0) {
//      throw new Error(`[registerService] validateClientMetadata invalid grantTypes ${grantTypes}`);
//    }
//    if (Arrays.stream(validationResultDto.getResponseTypes()).filter(responseType -> !config.responseTypes.includes(responseType)).length > 0) {
//      throw new Error(`[registerService] validateClientMetadata invalid responseTypes ${responseTypes}`);
//    }

    return validationResultDto;
  }

  public Client update(RequestClientDto requestClientDto, ResponseClientDto responseClientDto, ClientValidationResultDto validationResultDto) {

    // todo: compare requestClientDto and responseClientDto should be done by another function
    if (!requestClientDto.getClientId().equals(responseClientDto.getClientId())) {
      // todo: throw new Error(`${util.fetchCurrentDatetimeJst()} [updateClientLogic.updateClient] invalid clientId`);
    }
    if(requestClientDto.getClientSecret() != null && !requestClientDto.getClientSecret().equals(responseClientDto.getClientSecret())) {
      // todo: throw new Error(`${util.fetchCurrentDatetimeJst()} [updateClientLogic.updateClient] invalid clientSecret`);
    }

    Client clientToBeUpdated = new Client(
        requestClientDto.getClientId(),
        requestClientDto.getClientSecret(),
        requestClientDto.getClientName(),
        validationResultDto.getTokenEndpointAuthMethod() != null ? validationResultDto.getTokenEndpointAuthMethod() : requestClientDto.getTokenEndpointAuthMethod(),
        requestClientDto.getClientUri(),
        String.join(" ", requestClientDto.getRedirectUris()),
        validationResultDto.getGrantTypes() != null ? String.join(" ", validationResultDto.getGrantTypes()) : String.join(" ", requestClientDto.getGrantTypes()),
        validationResultDto.getResponseTypes() != null ? String.join(" ", validationResultDto.getResponseTypes()) : String.join(" ", requestClientDto.getResponseTypes()),
        String.join(" ", requestClientDto.getScopes()),
        RandomStringUtils.random(8, true, true),
        requestClientDto.getRegistrationClientUri()
    );

    Client clientUpdated = this.clientLogic.registerClient(clientToBeUpdated);

    this.logger.log("RegisterClientService", "clientUpdated = " + clientUpdated.toString());

    return clientUpdated;
  }
}
