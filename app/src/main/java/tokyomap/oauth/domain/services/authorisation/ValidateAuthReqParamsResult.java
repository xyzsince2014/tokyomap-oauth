package tokyomap.oauth.domain.services.authorisation;

import tokyomap.oauth.domain.entities.postgres.Client;

class ValidateAuthReqParamsResult {
  private Client client;
  private String[] requestedScope;

  ValidateAuthReqParamsResult(Client client, String[] requestedScope) {
    this.client = client;
    this.requestedScope = requestedScope;
  }

  Client getClient() {
    return client;
  }

  String[] getRequestedScope() {
    return requestedScope;
  }
}
