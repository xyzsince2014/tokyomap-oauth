package tokyomap.oauth.domain.services.authorisation;

import org.springframework.stereotype.Service;
import tokyomap.oauth.dtos.PreAuthoriseDto;

@Service
public class PreAuthorisationService {

  /**
   * validates the given request params, then cache them in the Redis
   */
  public PreAuthoriseDto execute() {
    // todo: logging `${util.fetchCurrentDatetimeJst()} [preAuthoriseService.execute]` with aspect

    String[] result = this.valdiateParams();

    String requestId = this.registerParams(
        // todo: authReqParams
    );

    return new PreAuthoriseDto(result[0], result[1], requestId);
  }

  private String[] valdiateParams(
      // todo: AuthReqParams authReqParams
  ) {
    // todo: console.log(`${util.fetchCurrentDatetimeJst()} [preAuthoriseLogic.validateParams] authReqParams = ${JSON.stringify(authReqParams)}`);

    // todo: fetch a registered client from t_client, check the it's redirectUris
    //  const client = await clientLogic.getClient(pgClient, {clientId: authReqParams.clientId});
    //  if (!client) {
    //    throw new Error('invalid client');
    //  }
    //  if(client.redirectUris.indexOf(authReqParams.redirectUri) === -1) {
    //    throw new Error('invalid redirectUri');
    //  }
    //  const requestedScope = authReqParams.scope.split(" ");
    //  const scope = client.scope.split(" ");
    //  if (!util.isObjectlncluded(requestedScope, scope)) {
    //    throw new Error('invalid scope requested');
    //  }

    return new String[] {"client", "requestedScope"};
  }

  private String registerParams(
      // todo: authReqParams
  ) {
    // todo: const requestId = await authReqParamsLogic.registerParams(authReqParams);
    return "requestId";
  }
}
