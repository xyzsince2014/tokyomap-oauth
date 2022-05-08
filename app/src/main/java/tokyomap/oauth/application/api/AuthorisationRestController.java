package tokyomap.oauth.application.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authorise")
public class AuthorisationRestController {
//  private final ProAuthorisationService proAuthorisationService;
//
//  @Autowired
//  public AuthorisationRestController(PreAuthoriseService proAuthorisationService) {
//    this.proAuthorisationService = proAuthorisationService;
//  }

// todo: @RequestMapping(method = RequestMethod.POST)
  @RequestMapping(method = RequestMethod.GET)
  public String proAuthorise() {
//    PreAuthoriseResponseDto dto = this.proAuthorisationService.execute();
    return "hoge";
  }
}
