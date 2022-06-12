package tokyomap.oauth.application.introspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.domain.services.introspect.IntrospectService;
import tokyomap.oauth.dtos.RequestIntrospectDto;

@RestController
@RequestMapping("/introspect")
public class IntrospectRestController {

  private final IntrospectService introspectService;

  @Autowired
  public IntrospectRestController(IntrospectService introspectService) {
    this.introspectService = introspectService;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public Boolean introspect(RequestIntrospectDto requestDto, @RequestHeader("Authorization") String authorization) {
    Boolean isActive = this.introspectService.introspect(requestDto.getToken(), authorization);
    return isActive;
  }
}
