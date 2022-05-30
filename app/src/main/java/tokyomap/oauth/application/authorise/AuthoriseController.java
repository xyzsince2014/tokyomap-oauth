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
import tokyomap.oauth.domain.entities.redis.AuthReqParams;
import tokyomap.oauth.domain.services.authorise.PreAuthoriseDomainService;
import tokyomap.oauth.domain.services.authorise.ProAuthoriseDomainService;
import tokyomap.oauth.dtos.PreAuthoriseResponseDto;
import tokyomap.oauth.utils.Logger;

@Controller
@RequestMapping("/authorise")
public class AuthoriseController {

  private final PreAuthoriseDomainService preAuthoriseDomainService;
  private final ProAuthoriseDomainService proAuthoriseDomainService;
  private final Logger logger;

  @Autowired
  public AuthoriseController(PreAuthoriseDomainService preAuthoriseDomainService, ProAuthoriseDomainService proAuthoriseDomainService, Logger logger) {
    this.preAuthoriseDomainService = preAuthoriseDomainService;
    this.proAuthoriseDomainService = proAuthoriseDomainService;
    this.logger = logger;
  }

  @ModelAttribute("authorisationForm")
  public AuthorisationForm setUpForm() {
    return new AuthorisationForm();
  }

  /**
   * http://localhost/authorise?responseType=AUTHORISATION_CODE&scope=read%20write%20delete%20openid%20profile%20email%20address%20phone&clientId=sLoBOeuIkRtEH7rXmQeCjeuc8Iz4ub1t&redirectUri=http%3A%2F%2Flocalhost%3A9000%2Fcallback&state=QFqFnUqlmEZZOPiFqymQ08tPXUiS2DuG&codeChallenge=xBENteFcVZvujKNgFJ7k9E8WLKrdUoZ16OdyB4axOmk&codeChallengeMethod=SHA256
   * @param model
   * @param reqParams
   * @return String
   */
  @RequestMapping(method = RequestMethod.GET)
  public String preAuthorise(Model model, @RequestParam Map<String,String> reqParams) {

    AuthReqParams authReqParams = new AuthReqParams(
        reqParams.get("responseType"), reqParams.get("scope"), reqParams.get("clientId"),
        reqParams.get("redirectUri"), reqParams.get("state"), reqParams.get("codeChallenge"), reqParams.get("codeChallengeMethod")
    );

    PreAuthoriseResponseDto dto = this.preAuthoriseDomainService.execute(authReqParams);

    /**
     * todo: OAuth Authentication with Modal
     * * distribute a js beforehand
     * * clients use the js
     * * authentication actions trigger a function of the js
     * * the function display an Authentication form in a modal
     * * use Multi-Factor Authentication
     * * submit the form request to the pro-authorisaton endpoint
     */
    model.addAttribute("dto", dto);

    return "authorise";
  }

  /**
   * authorise requests from authorise.html, issue an Authorisation Code, and redirect to callback endpoints
   * @param authorisationForm
   * @return String
   */
  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public String proAuthorise(@Validated AuthorisationForm authorisationForm) {

    URI redirectUri = this.proAuthoriseDomainService.execute(authorisationForm);

    return "redirect:" + redirectUri.toString();
  }
}
