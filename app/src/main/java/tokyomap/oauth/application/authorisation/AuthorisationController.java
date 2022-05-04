package tokyomap.oauth.application.authorisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tokyomap.oauth.domain.services.authorisation.PreAuthorisationService;
import tokyomap.oauth.dtos.PreAuthoriseDto;

@Controller
@RequestMapping("/authorise")
public class AuthorisationController {

  private final PreAuthorisationService preAuthorisationService;

  @Autowired
  public AuthorisationController(PreAuthorisationService preAuthorisationService) {
    this.preAuthorisationService = preAuthorisationService;
  }

  @RequestMapping(method = RequestMethod.GET)
  public String preAuthorise(Model model) {
    PreAuthoriseDto dto = this.preAuthorisationService.execute();

    // todo: instead of rendering a view, return to clients a JS module to authenticate
    model.addAttribute("dto", dto);
    return "authenticate";
  }
}
