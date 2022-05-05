package domain.services.authorisation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tokyomap.oauth.RedisClientConfig;
import tokyomap.oauth.WebMvcConfig;
import tokyomap.oauth.domain.entities.redis.AuthReqParams;
import tokyomap.oauth.domain.services.authorisation.PreAuthorisationService;
import tokyomap.oauth.dtos.authorisation.PreAuthoriseDto;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {WebMvcConfig.class, RedisClientConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class PreAuthorisationIntegrationTest {

  @Autowired
  private PreAuthorisationService preAuthorisationService; // the test target Bean

  @Test
  public void testExecute() {
    AuthReqParams authReqParams = new AuthReqParams(
        "AUTHORISATION_CODE",
        "read write delete openid profile email address phone",
        "sLoBOeuIkRtEH7rXmQeCjeuc8Iz4ub1t",
        "http://localhost:9000/callback",
        "Dxyd94mc0D4GvbC5m6pxjpqnKIwuzHn3",
        "7gS7IusXI3JCG8EE8C7p70yo7O7qJc8hG-Lrs6fj_0o",
        "SHA256"
    );

    PreAuthoriseDto actualDto = this.preAuthorisationService.execute(authReqParams);

    assertThat(actualDto.getClient().getClientId()).isEqualTo("sLoBOeuIkRtEH7rXmQeCjeuc8Iz4ub1t");
    assertThat(actualDto.getRequestId()).isNotNull();
    assertThat(actualDto.getRequestedScope()).isEqualTo(new String[] {"read", "write", "delete", "openid", "profile", "email", "address", "phone"});
  }
}
