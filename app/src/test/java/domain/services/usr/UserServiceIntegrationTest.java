package domain.services.usr;

import static org.assertj.core.api.Assertions.assertThat;

import tokyomap.oauth.PostgresJpaConfig;
import tokyomap.oauth.WebMvcConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.services.usr.UsrService;

@RunWith(SpringRunner.class) // the Runner class to run the test DI container
@ContextConfiguration(classes = {WebMvcConfig.class, PostgresJpaConfig.class}) // the test DI container's configs
@WebAppConfiguration // enable injections of DI container for web app and mock objects dependant of Servlet API
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

  @Autowired
  private UsrService usrService; // the test target Bean

  @Test
  public void testFindUserByUserId() {
    Usr actual = this.usrService.findUsrBySub("00u1sneigDs6Rolmu2p6");

    assertThat(actual.getName()).isEqualTo("Ken Vinson");
    assertThat(actual.getPicture()).isNull();
    assertThat(actual.getAddress()).isEqualTo("{\"streetAddress\":\"999 Patpong Street\", \"locality\": \"unknown\", \"region\": \"Central Thailand\", \"postalCode\": \"10###\", \"country\": \"Thailand\"}");
  }

  // todo: test the other functions
}
