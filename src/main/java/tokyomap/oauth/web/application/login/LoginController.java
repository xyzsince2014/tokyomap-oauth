package tokyomap.oauth.web.application.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/login")
public class LoginController {
  @GetMapping
  public String index() {
    return "/login/loginForm";
  }
}
