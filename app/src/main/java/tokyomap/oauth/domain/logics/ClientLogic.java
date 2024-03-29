package tokyomap.oauth.domain.logics;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.repositories.postgres.ClientRepository;

@Component
public class ClientLogic {

  private ClientRepository clientRepository;

  @Autowired
  public ClientLogic(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  /**
   * register the client given
   * @param client
   * @return clientRegistered
   */
  public Client registerClient(Client client) {
    Client clientRegistered = this.clientRepository.saveAndFlush(client);
    return clientRegistered;
  }

  /**
   * get the client for the given sub
   * @param clientId
   * @return Optional<Client>
   */
  public Client getClientByClientId(String clientId) {
    Optional<Client> optionalClient = this.clientRepository.findById(clientId);
    return optionalClient.orElse(null);
  }

  /**
   * unregister the client for the given clientId
   * @param clientId
   */
  public void unregisterClient(String clientId) {
    this.clientRepository.deleteById(clientId);
  }
}
