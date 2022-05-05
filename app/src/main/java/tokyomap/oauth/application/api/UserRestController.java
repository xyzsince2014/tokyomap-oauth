package tokyomap.oauth.application.api;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.services.usr.UsrService;

// todo: @CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserRestController {

  private final UsrService usrService;

  @Autowired
  public UserRestController(UsrService usrService) {
    this.usrService = usrService;
  }

  /**
   * curl -v -X GET "http://localhost:80/api/user/{sub}"
   * @param sub
   * @return UserDto
   */
  @RequestMapping(path = "/{sub}", method = RequestMethod.GET)
  public UserDto getUser(@PathVariable String sub) {
    Usr usr = this.usrService.findUsrBySub(sub);
    UserDto dto = new UserDto(usr);
    return dto;
  }

  /**
   * curl -v -X GET "http://localhost:80/api/user"
   * @return List<UserDto>
   */
  @RequestMapping(method = RequestMethod.GET)
  public List<UserDto> getAllUsers() {
    List<Usr> usrList = this.usrService.findAll();
    return usrList.stream().map(UserDto::new).collect(Collectors.toList());
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

    // todo: refine usr
    Usr usr = new Usr();
    usr.setSub(dto.getSub());
    usr.setFamilyName(dto.getFamilyName());
    usr.setScope(dto.getScope());
    Usr usrCreated = this.usrService.save(usr);

    URI resourceUri = uriBuilder
        .path("/api/user/{sub}")
        .buildAndExpand(usrCreated.getSub()) // build a UriComponents instance and replaces URI template variables with the values from an array
        .encode()
        .toUri();

    return ResponseEntity.created(resourceUri).build();
  }

  /**
   * curl -i -X PUT "http://localhost:80/api/user/hoge" -H "Content-Type: application/json" -d '{"userId":"fuga","givenName":"foo","familyName":"boo"}'
   * @param sub
   * @param dto
   */
  @RequestMapping(path = "/{sub}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT) // return "204 No Content"
  public void updateUser(
      @PathVariable String sub,
      @Validated @RequestBody UserDto dto
  ) {

    // todo: refine usr
    Usr usr = this.usrService.findUsrBySub(sub);
    usr.setFamilyName(dto.getFamilyName());
    usr.setGivenName(dto.getGivenName());

    this.usrService.save(usr);
  }

  /**
   * curl -v -X DELETE "http://localhost:80/api/user/{sub}"
   * @param sub
   */
  @RequestMapping(path = "/{sub}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable String sub) {
    this.usrService.deleteUsrBySub(sub);
  }
}
