package tokyomap.oauth.application.authorise;

import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tokyomap.oauth.domain.entities.redis.PreAuthoriseCache;
import tokyomap.oauth.domain.services.authorise.PreAuthoriseService;
import tokyomap.oauth.domain.services.authorise.ProAuthoriseService;
import tokyomap.oauth.dtos.PreAuthoriseResponseDto;
import tokyomap.oauth.utils.Logger;

@Controller
@RequestMapping("/authorise")
public class AuthoriseController {

  private final PreAuthoriseService preAuthoriseService;
  private final ProAuthoriseService proAuthoriseService;
  private final Logger logger;

  @Autowired
  public AuthoriseController(PreAuthoriseService preAuthoriseService, ProAuthoriseService proAuthoriseService, Logger logger) {
    this.preAuthoriseService = preAuthoriseService;
    this.proAuthoriseService = proAuthoriseService;
    this.logger = logger;
  }

  @ModelAttribute("authorisationForm")
  public AuthorisationForm setUpForm() {
    return new AuthorisationForm();
  }

  /**
   * validate the authorisation request, and return the authorisation page
   * @param model
   * @param queryParams
   * @return String
   */
  @RequestMapping(method = RequestMethod.GET)
  public String preAuthorise(Model model, @RequestParam Map<String, String> queryParams) {

    PreAuthoriseCache preAuthoriseCache = new PreAuthoriseCache(
        queryParams.get("responseType"), queryParams.get("scopes").split(" "), queryParams.get("clientId"),
        queryParams.get("redirectUri"), queryParams.get("state"), queryParams.get("codeChallenge"), queryParams.get("codeChallengeMethod")
    );

    PreAuthoriseResponseDto responseDto = this.preAuthoriseService.execute(preAuthoriseCache);

    /**
     * todo: OAuth Authentication with Modal
     * * distribute a js beforehand
     * * clients use the js
     * * authentication actions trigger a function of the js
     * * the function display an Authentication form in a modal
     * * use Multi-Factor Authentication
     * * submit the form request to the pro-authorisaton endpoint
     */
    model.addAttribute("dto", responseDto);

    return "authorise";
  }

  /**
   * authorise requests from authorise.html, issue an Authorisation Code, and redirect to callback endpoints
   * @param authorisationForm
   * @return String
   */
  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public String proAuthorise(@Validated AuthorisationForm authorisationForm) {
    URI redirectUri = this.proAuthoriseService.execute(authorisationForm);
    return "redirect:" + redirectUri.toString();
  }
}
