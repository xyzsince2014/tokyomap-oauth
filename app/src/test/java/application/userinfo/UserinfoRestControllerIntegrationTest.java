package application.userinfo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {WebMvcConfig.class})
@WebAppConfiguration
@ActiveProfiles("develop")
public class UserinfoRestControllerIntegrationTest {

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
        .perform(get("/userinfo/9XE3-JI34-99999A").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  public void testGetAllUsers() throws Exception {
    this.mockMvc
        .perform(get("/userinfo").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

// todo:
//  @Test
//  public void testCreateUser() throws Exception {
//    UserInfoDto dto = new UserInfoDto();
//    dto.setSub("hoge");
//    dto.setFamilyName("fuga");
//    dto.setScope("hoge fuga");
//
//    this.mockMvc
//        .perform(post("/user", dto).accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isCreated())
//        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//  }

// todo:
//  @Test
//  public void testUpdateUser() throws Exception {
//    UserInfoDto dto = new UserInfoDto();
//    dto.setSub("hoge");
//    dto.setFamilyName("foo");
//    dto.setGivenName("boo");
//
//    this.mockMvc
//        .perform(put("/user", dto).accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isNoContent());
//  }

// todo:
//  @Test
//  public void testDeleteUser() throws Exception {
//    this.mockMvc
//        .perform(delete("/user/hoge"))
//        .andExpect(status().isNoContent());
//  }
}
