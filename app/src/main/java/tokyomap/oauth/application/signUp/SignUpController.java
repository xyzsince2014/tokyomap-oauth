package tokyomap.oauth.application.signUp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tokyomap.oauth.domain.services.signUp.SignUpException;
import tokyomap.oauth.domain.services.signUp.SignUpService;

@Controller
@RequestMapping("/sign-up")
public class SignUpController {

  private SignUpService signUpService;

  @Autowired
  public SignUpController(SignUpService signUpService) {
    this.signUpService = signUpService;
  }

  @ModelAttribute("signUpForm")
  public SignUpForm setUpForm() {
    return new SignUpForm();
  }

  @RequestMapping(method = RequestMethod.GET)
  public String preSingUp() {
    return "signUp";
  }

  @RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded;charset=utf-8")
  public String proSingUp(@Validated SignUpForm signUpForm) {
    try {
      this.signUpService.execute(signUpForm);
      return "redirect:/authenticate"; // todo: directly sign in after signing up
    } catch (SignUpException e) {
      return "error"; // todo: handle properly
    }
  }
}
