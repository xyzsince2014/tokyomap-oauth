package application.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tokyomap.oauth.WebMvcConfig;
import tokyomap.oauth.application.api.UserDto;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {WebMvcConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class UserRestControllerIntegrationTest {
  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Before
  public void setUpMockMvc() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
  }

  @Test
  public void testGetUser() throws Exception {

    String res = this.mockMvc
        .perform(get("/api/users/bbbb").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    UserDto actualDto = (new ObjectMapper()).readValue(res, UserDto.class);

    assertThat(actualDto.getUserId()).isEqualTo("bbbb");
    assertThat(actualDto.getGivenName()).isEqualTo("Bbb");
    assertThat(actualDto.getFamilyName()).isEqualTo("Bbb");
    assertNull(actualDto.getCreatedAt());
  }
}
