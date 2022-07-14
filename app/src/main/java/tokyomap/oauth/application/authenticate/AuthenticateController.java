package tokyomap.oauth.application.authenticate;

import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import tokyomap.oauth.domain.entities.redis.PreAuthoriseCache;
import tokyomap.oauth.domain.services.authenticate.AuthenticateService;

@Controller
@RequestMapping("/authenticate")
public class AuthenticateController {

  private AuthenticateService authenticateService;

  @Autowired
  public AuthenticateController(AuthenticateService authenticateService) {
    this.authenticateService = authenticateService;
  }

  /**
   * cache the given query params before returning the form to authenticate
   * @return the login form
   */
  @RequestMapping(path = "/pre", method = RequestMethod.GET)
  public String preAuthenticate() {
    return "authenticate";
  }

  @RequestMapping(path = "/error", method = RequestMethod.GET)
  public String error() {
    return "error";
  }
}
