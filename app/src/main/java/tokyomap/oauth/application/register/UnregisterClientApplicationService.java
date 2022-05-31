package tokyomap.oauth.application.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.services.register.UnregisterClientDomainService;
import tokyomap.oauth.dtos.UnregisterClientRequestDto;

@Service
public class UnregisterClientApplicationService {

  private final UnregisterClientDomainService unregisterClientDomainService;

  @Autowired
  public UnregisterClientApplicationService(UnregisterClientDomainService unregisterClientDomainService) {
    this.unregisterClientDomainService = unregisterClientDomainService;
  }

  @Transactional
  public void execute(String clientId, UnregisterClientRequestDto requestDto) {
    this.unregisterClientDomainService.unregister(clientId, requestDto.getAccessToken(), requestDto.getRefreshToken());
  }
}
