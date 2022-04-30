package tokyomap.oauth.application.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login") // by which the dispatcherServlet selects the handler for an incoming request
public class LoginController {
  @GetMapping
  public String index() {
    return "/login/loginForm";
  }
}
