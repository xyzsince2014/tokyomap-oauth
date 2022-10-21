package tokyomap.oauth.application.authorise;

import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.entities.redis.PreAuthoriseCache;
import tokyomap.oauth.domain.services.authenticate.ResourceOwnerDetails;
import tokyomap.oauth.domain.services.authorise.InvalidPreAuthoriseException;
import tokyomap.oauth.domain.services.authorise.InvalidProAuthoriseException;
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
  public AuthorisationForm authorisationForm() {
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

    try {
      PreAuthoriseCache preAuthoriseCache = new PreAuthoriseCache(
          queryParams.get("responseType"), queryParams.get("scopes").split(" "),
          queryParams.get("clientId"), queryParams.get("redirectUri"), queryParams.get("state"), queryParams.get("codeChallenge"),
          queryParams.get("codeChallengeMethod"), queryParams.get("nonce")
      );

      // todo: use regex
      if (preAuthoriseCache.getRedirectUri() == null || preAuthoriseCache.getRedirectUri().equals("")) {
        return "error";
      }

      this.logger.log(AuthoriseController.class.getName() + "preAuthoriseCache = ", preAuthoriseCache.toString());

      PreAuthoriseResponseDto responseDto = this.preAuthoriseService.execute(preAuthoriseCache);
      model.addAttribute("dto", responseDto);
      return "authorise"; // todo: separate authentication from authorisation, use WebSecurityConfigurerAdapter ?

    } catch (NullPointerException e) {
      return "error";

    } catch(InvalidPreAuthoriseException e) {
      this.logger.log(AuthoriseController.class.getName() + "e.getMessage()", e.getMessage());
      model.addAttribute("clientUri", e.getClientUri());
      return "invalidRequest";
    }
  }

  /**
   * authorise requests from authorise.html, issue an Authorisation Code, and redirect to callback endpoints
   * @param authorisationForm
   * @return String
   */
  @RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded;charset=utf-8")
  public String proAuthorise(Model model, @Validated AuthorisationForm authorisationForm) {

    try {
      Usr resourceOwner = ((ResourceOwnerDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getResourceOwner();
      URI redirectUri = this.proAuthoriseService.execute(resourceOwner, authorisationForm);
      return "redirect:" + redirectUri.toString();

    } catch (InvalidProAuthoriseException e) {
      model.addAttribute("clientUri", authorisationForm.getClientUri());
      return "invalidAuthorisation";
    }
  }
}
