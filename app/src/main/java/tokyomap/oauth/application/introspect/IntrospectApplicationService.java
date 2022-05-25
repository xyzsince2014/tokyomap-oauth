package tokyomap.oauth.application.introspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.services.introspect.IntrospectDomainService;

@Service
public class IntrospectApplicationService {

  private final IntrospectDomainService introspectDomainService;

  @Autowired
  public IntrospectApplicationService(IntrospectDomainService introspectDomainService) {
    this.introspectDomainService = introspectDomainService;
  }

  public Boolean execute(RequestIntrospectDto requestDto, String authorization) {
    return this.introspectDomainService.introspect(requestDto.getToken(), authorization);
  }
}

