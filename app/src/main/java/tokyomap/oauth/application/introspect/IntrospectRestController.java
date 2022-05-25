package tokyomap.oauth.application.introspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/introspect")
public class IntrospectRestController {

  private final IntrospectApplicationService introspectApplicationService;

  @Autowired
  public IntrospectRestController(IntrospectApplicationService introspectApplicationService) {
    this.introspectApplicationService = introspectApplicationService;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public Boolean introspect(RequestIntrospectDto requestDto, @RequestHeader("Authorization") String authorization) {
    return this.introspectApplicationService.execute(requestDto, authorization);
  }
}
