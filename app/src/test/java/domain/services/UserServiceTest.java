package domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import tokyomap.oauth.domain.models.entities.Role;
import tokyomap.oauth.domain.models.entities.User;
import tokyomap.oauth.domain.repositories.user.UserRepository;
import tokyomap.oauth.domain.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

  // the test target
  @InjectMocks
  private UserService userService;

  // mock a Bean to be injected into the target
  @Mock
  private UserRepository userRepository;

  @BeforeEach
  public void initMocks() {
    // inject the mocked Bean into the test target
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void test_findByUserId() {
    final String userId = "999";
    final String password = "pass";

    // define the mock behaviour
    when(this.userRepository.findByUserId(userId)).thenReturn(new User(userId, password, null, null, Role.USER));

    User actual = this.userService.findUserByUserId(userId);

    assertThat(actual.getUserId()).isEqualTo(userId);
    assertThat(actual.getPassword()).isEqualTo(password);
    assertNull(actual.getGivenName());
    assertThat(actual.getRole()).isEqualTo(Role.USER);
  }
}
