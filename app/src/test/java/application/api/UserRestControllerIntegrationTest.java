package application.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    this.mockMvc
        .perform(get("/api/user/9XE3-JI34-99999A").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  public void testGetAllUsers() throws Exception {
    this.mockMvc
        .perform(get("/api/user").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

// todo:
//  @Test
//  public void testCreateUser() throws Exception {
//    UserDto dto = new UserDto();
//    dto.setSub("hoge");
//    dto.setFamilyName("fuga");
//    dto.setScope("hoge fuga");
//
//    this.mockMvc
//        .perform(post("/api/user", dto).accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isCreated())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//  }

// todo:
//  @Test
//  public void testUpdateUser() throws Exception {
//    UserDto dto = new UserDto();
//    dto.setSub("hoge");
//    dto.setFamilyName("foo");
//    dto.setGivenName("boo");
//
//    this.mockMvc
//        .perform(put("/api/user", dto).accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isNoContent());
//  }

// todo:
//  @Test
//  public void testDeleteUser() throws Exception {
//    this.mockMvc
//        .perform(delete("/api/user/hoge"))
//        .andExpect(status().isNoContent());
//  }
}
