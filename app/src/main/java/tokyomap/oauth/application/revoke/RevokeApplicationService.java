package tokyomap.oauth.application.revoke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.services.revoke.RevokeDomainService;
import tokyomap.oauth.dtos.RevokeRequestDto;

// todo: merge application services to their controllers
@Service
public class RevokeApplicationService {

  private final RevokeDomainService revokeDomainService;

  @Autowired
  public RevokeApplicationService(RevokeDomainService revokeDomainService) {
    this.revokeDomainService = revokeDomainService;
  }

  public void execute(RevokeRequestDto requestDto, String authorization) {
    this.revokeDomainService.revoke(requestDto, authorization);
  }
}
