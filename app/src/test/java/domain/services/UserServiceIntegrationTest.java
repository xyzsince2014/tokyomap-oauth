package domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tokyomap.oauth.JpaConfig;
import tokyomap.oauth.WebMvcConfig;
import tokyomap.oauth.domain.models.entities.Role;
import tokyomap.oauth.domain.models.entities.User;
import tokyomap.oauth.domain.services.user.UserService;

@RunWith(SpringRunner.class) // the Runner class to run the test DI container
@ContextConfiguration(classes = {WebMvcConfig.class, JpaConfig.class}) // the test DI container's configs
@WebAppConfiguration // enable injections of DI container for web app and mock objects dependant of Servlet API
@ActiveProfiles("test")
public class UserServiceIntegrationTest {
  @Autowired
  private UserService userService; // the test target Bean

  @Test
  public void testFindUserByUserId() throws Exception {
    User actual = this.userService.findUserByUserId("hoge-fuga");

    assertThat(actual.getFamilyName()).isEqualTo("fuga");
    assertThat(actual.getGivenName()).isEqualTo("hoge");
    assertThat(actual.getRole()).isEqualTo(Role.ADMIN);
  }
}
