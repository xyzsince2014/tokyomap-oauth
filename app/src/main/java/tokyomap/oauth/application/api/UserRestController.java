package tokyomap.oauth.application.api;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import tokyomap.oauth.domain.models.entities.Role;
import tokyomap.oauth.domain.models.entities.User;
import tokyomap.oauth.domain.services.user.UserService;


@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserRestController {

  private static final String PASSWORD = "$2a$12$QYHQH.cyIYGZFWJ7m0BTrO.Yc5W54E58GdjQygbAmHyyaNC89/Qxq";

  private final UserService userService;

  @Autowired
  UserRestController(UserService userService) {
    this.userService = userService;
  }

  /**
   * curl -v -X GET "http://localhost:80/api/user/hoge"
   * @param userId
   * @return
   */
  @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
  public UserDto getUser(@PathVariable String userId) throws Exception {
    User user = this.userService.findUserByUserId(userId);

    UserDto dto = new UserDto();
    dto.setUserId(user.getUserId());
    dto.setGivenName(user.getGivenName());
    dto.setFamilyName(user.getFamilyName());
//    dto.setCreatedAt(LocalDate.now()); // todo: malfunctioning, fix

    return dto;
  }

  /**
   * curl -v -X GET "http://localhost:80/api/user"
   * @return
   */
  @RequestMapping(method = RequestMethod.GET)
  public List<UserDto> searchAdmins() {
    List<User> userList = this.userService.findAdmins();

    return userList.stream()
        .map(admin -> {
          UserDto dto = new UserDto(admin.getUserId(), admin.getGivenName(), admin.getFamilyName());
          return dto;
        })
        .collect(Collectors.toList()); // return "200 OK"
  }

  /**
   * curl -v -X POST "http://localhost:80/api/user" -H "Content-Type: application/json" -d '{"userId":"fuga","givenName":"ueno","familyName":"tokyo"}'
   * @param dto
   * @return ResponseEntity<Void>
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> createUser(
      @Validated @RequestBody UserDto dto,
      UriComponentsBuilder uriBuilder
  ) {

    User user = new User();
    user.setUserId(dto.getUserId());
    user.setFamilyName(dto.getFamilyName());
    user.setGivenName(dto.getGivenName());
    user.setPassword(PASSWORD);
    user.setRole(Role.USER);

    User userCreated = this.userService.saveUser(user);

    URI resourceUri = uriBuilder
        .path("/api/user/{userId}")
        .buildAndExpand(userCreated.getUserId()) // build a UriComponents instance and replaces URI template variables with the values from an array
        .encode()
        .toUri();

    return ResponseEntity.created(resourceUri).build();
  }

  /**
   * curl -i -X PUT "http://localhost:80/api/user/hoge" -H "Content-Type: application/json" -d '{"userId":"fuga","givenName":"foo","familyName":"boo"}'
   * @param userId
   * @param dto
   */
  @RequestMapping(path = "/{userId}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT) // return "204 No Content"
  public void putUser(
      @PathVariable String userId,
      @Validated @RequestBody UserDto dto
  )  throws Exception {

    User user = this.userService.findUserByUserId(userId);

    user.setFamilyName(dto.getFamilyName());
    user.setGivenName(dto.getGivenName());
    user.setRole(Role.ADMIN);

    this.userService.saveUser(user);
  }

  /**
   * curl -v -X DELETE "http://localhost:80/api/user/hoge"
   * @param userId
   */
  @RequestMapping(path = "/{userId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable String userId) {
    this.userService.deleteUserByUserId(userId);
  }
}
