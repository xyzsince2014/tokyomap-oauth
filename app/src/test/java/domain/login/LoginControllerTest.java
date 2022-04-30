package domain.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tokyomap.oauth.application.login.LoginController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

public class LoginControllerTest {

  private MockMvc mockMvc;

  @InjectMocks
  LoginController loginController;

  @Before
  public void setUpMockMvc() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders
        .standaloneSetup(this.loginController)
        .addFilter(new CharacterEncodingFilter("UTF-8")) // add a Servlet filter
        .build();
  }

  /**
   * make MockMvc perform a GET request to /login
   * @throws Exception
   */
  @Test
  public void testIndex() throws Exception {
    this.mockMvc
        .perform(get("/login"))
        .andExpect(status().isOk())
        .andExpect(forwardedUrl("/login/loginForm"));
  }
}
