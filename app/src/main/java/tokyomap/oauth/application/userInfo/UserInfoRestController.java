package tokyomap.oauth.application.userInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.services.usr.UsrDominService;
import tokyomap.oauth.utils.JsonMapper;
import tokyomap.oauth.utils.Logger;

// todo: @CrossOrigin
@RestController
@RequestMapping("/userinfo")
public class UserInfoRestController {

  private final UsrDominService usrDominService;
  private final Logger logger;
  private final JsonMapper jsonMapper;

  @Autowired
  public UserInfoRestController(UsrDominService usrDominService, Logger logger, JsonMapper jsonMapper) {
    this.usrDominService = usrDominService;
    this.logger = logger;
    this.jsonMapper = jsonMapper;
  }

  /**
   * curl -v -X GET "http://localhost:80/userinfo/{sub}"
   * @param sub
   * @return UserInfoDto
   */
  @RequestMapping(path = "/{sub}", method = RequestMethod.GET)
  public UserInfoDto getUser(@PathVariable String sub) {
    Usr usr = this.usrDominService.findUsrBySub(sub);
    UserInfoDto dto = new UserInfoDto(usr);
    return dto;
  }

//  /**
//   * curl -v -X GET "http://localhost:80/userinfo"
//   * @return List<UserInfoDto>
//   */
//  @RequestMapping(method = RequestMethod.GET)
//  public List<UserInfoDto> getAllUsers() {
//    List<Usr> usrList = this.usrDominService.findAll();
//    return usrList.stream().map(UserInfoDto::new).collect(Collectors.toList());
//  }

  /**
   * curl -v -X POST "http://localhost:80/user" -H "Content-Type: application/json" -d '{"userId":"fuga","givenName":"ueno","familyName":"tokyo"}'
   * @param dto
   * @return ResponseEntity<Void>
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> createUser(
      @Validated @RequestBody UserInfoDto dto,
      UriComponentsBuilder uriBuilder
  ) {

    // todo: refine usr
    Usr usr = new Usr();
    usr.setSub(dto.getSub());
    usr.setFamilyName(dto.getFamilyName());
    usr.setScope(dto.getScope());
    Usr usrCreated = this.usrDominService.save(usr);

    URI resourceUri = uriBuilder
        .path("/userinfo/{sub}")
        .buildAndExpand(usrCreated.getSub()) // build a UriComponents instance and replaces URI template variables with the values from an array
        .encode()
        .toUri();

    return ResponseEntity.created(resourceUri).build();
  }

  /**
   * curl -i -X PUT "http://localhost:80/userinfo/hoge" -H "Content-Type: application/json" -d '{"userId":"fuga","givenName":"foo","familyName":"boo"}'
   * @param sub
   * @param dto
   */
  @RequestMapping(path = "/{sub}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT) // return "204 No Content"
  public void updateUser(
      @PathVariable String sub,
      @Validated @RequestBody tokyomap.oauth.application.userInfo.UserInfoDto dto
  ) {

    // todo: refine usr
    Usr usr = this.usrDominService.findUsrBySub(sub);
    usr.setFamilyName(dto.getFamilyName());
    usr.setGivenName(dto.getGivenName());

    this.usrDominService.save(usr);
  }

  /**
   * curl -v -X DELETE "http://localhost:80/userinfo/{sub}"
   * @param sub
   */
  @RequestMapping(path = "/{sub}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable String sub) {
    this.usrDominService.deleteUsrBySub(sub);
  }


  private Function<Map<String, Object>, Map<String, Object>> createUserInfo(String scope) {
    switch(scope) {
        // cases here are standardised OAuth Scopes
        case "openid": {
          return (Map<String, Object> usrMap) -> {
            HashMap<String, Object> userInfoMap = new HashMap();
            userInfoMap.put("sub", usrMap.get("sub"));
            return userInfoMap;
          };
        }
        case "profile": {
          return (Map<String, Object> usrMap) -> {
            HashMap<String, Object> userInfoMap = new HashMap();
            userInfoMap.put("name", usrMap.get("name"));
            userInfoMap.put("familyName", usrMap.get("familyName"));
            userInfoMap.put("givenName", usrMap.get("givenName"));
            userInfoMap.put("middleName", usrMap.get("middleName"));
            userInfoMap.put("nickname", usrMap.get("nickname"));
            userInfoMap.put("preferredUsername", usrMap.get("preferredUsername"));
            userInfoMap.put("profile", usrMap.get("picture"));
            userInfoMap.put("website", usrMap.get("website"));
            userInfoMap.put("gender", usrMap.get("gender"));
            userInfoMap.put("birthdate", usrMap.get("birthdate"));
            userInfoMap.put("zoneinfo", usrMap.get("zoneinfo"));
            userInfoMap.put("locale", usrMap.get("locale"));
            userInfoMap.put("updatedAt", usrMap.get("updatedAt"));
            return userInfoMap;
          };
        }
        case "email": {
          return (Map<String, Object> usrMap) -> {
            HashMap<String, Object> userInfoMap = new HashMap();
            userInfoMap.put("email", usrMap.get("email"));
            userInfoMap.put("emailVerified", usrMap.get("emailVerified"));
            return userInfoMap;
          };
        }
        case "address": {
          return (Map<String, Object> usrMap) -> {
            HashMap<String, Object> userInfoMap = new HashMap();
            userInfoMap.put("address", usrMap.get("address"));
            return userInfoMap;
          };
        }
        case "phone": {
          return (Map<String, Object> usrMap) -> {
            HashMap<String, Object> userInfoMap = new HashMap();
            userInfoMap.put("phoneNumber", usrMap.get("phoneNumber"));
            userInfoMap.put("phoneNumberVerified", usrMap.get("phoneNumberVerified"));
            return userInfoMap;
          };
        }
        default: {
          return (Map<String, Object> usrMap) -> {
            return new HashMap();
          };
        }
      }
    }

  /**
   * get UserInfoDto
   * @return
   */
  @RequestMapping(method = RequestMethod.GET)
  public tokyomap.oauth.application.userInfo.UserInfoDto getUserinfo(@RequestHeader("Authorization") String authorization) {

    // todo: do the things below in the app service layer
    AccessTokenDto accessTokenDto = this.checkAccessToken(authorization);

    if (!Arrays.stream(accessTokenDto.getScope()).anyMatch("openid"::equals)) {
      this.logger.log("UserInfoRestController", "no openid in the scope");
      return null; // todo: return 403
    }

    // todo: return NOT_FOUND in case user is not found
    Usr usr = this.usrDominService.findUsrBySub(accessTokenDto.getSub());

    Map<String, Object> usrMap = usr.convertToMap();

    Map<String, Object> userInfoMap = Arrays.asList(accessTokenDto.getScope()).stream().reduce(new HashMap(), (acc, scope) -> {
          Map<String, Object> cushion = this.createUserInfo(scope).apply(usrMap);
          for(String prop : cushion.keySet()) {
            try {
              acc.merge(prop, cushion.get(prop), (a, b) -> cushion.get(prop));
            }
            // todo: handle NullPointerException properly
            catch (NullPointerException e) {
              this.logger.log("UserInfoRestController", "NullPointerException scope = " + scope + ", prop = " + prop);
            }
          }
          return acc;
          },
          (a, b) -> null
      );

    logger.log("UserInfoRestContorller", "userInfoMap = " + userInfoMap.toString());

    UserInfoDto dto = new UserInfoDto(userInfoMap);

    logger.log("UserInfoRestContorller", "UserInfoDto = " + dto.toString());

    return dto;
  }

  /**
   * check the access token in the Authorization header
   * @param authorization
   */

  private AccessTokenDto checkAccessToken(String authorization) {

    String incomingToken =
        authorization.toLowerCase().indexOf("bearer") == 0 // converts the value of the header to lowercase too
        ? authorization.substring("bearer ".length()) // token value itself is case sensitive, hence we slice the original string, not a transformed one
        : null;

    if(incomingToken == null) {
      this.logger.log("UserInfoRestController", "no matching token for incomingToken");
      // throw new Error(`no matching token for incomingToken`);
    }

    String[] incomingTokenSplit = incomingToken.split("\\.");
    String header = new String((new Base64()).decode(incomingTokenSplit[0].getBytes()));
    String payload = new String((new Base64()).decode(incomingTokenSplit[1].getBytes()));

    this.logger.log("UserInfoRestController", "access token header = " + header + ", access token payload = " + payload);

    // todo: inquire to the introspection endpoint
//    const introspectionResponse = await fetch(config.authServer.introspectionEndpoint, {
//        method: 'POST',
//        mode: 'cors',
//        headers: {
//      'Authorization': 'Basic ' + util.encodeClientCredentials(config.resource.resourceId, config.resource.resourceSecret),
//          'Content-Type': 'application/x-www-form-urlencoded',
//    },
//    body: util.createRequestBody({token: incomingToken}),
//    });
//
//    if(!introspectionResponse.ok) {
//      throw new Error(`introspection failed`);
//    }
//
//    const isActive = await introspectionResponse.json();
//
//    if (!isActive) {
//      throw new Error(`token inactive`);
//    }
//

    Map<String, Object> map =  this.jsonMapper.convertJsonStringToMap(payload);
    ArrayList<String> audList = (ArrayList<String>) map.get("aud");
    ArrayList<String> scopeList = (ArrayList<String>) map.get("scope");
    AccessTokenDto dto = new AccessTokenDto(
        (String) map.get("iss"),
        (String) map.get("sub"),
        audList != null ? audList.toArray(new String[audList.size()]) : null,
        (String) map.get("lat"),
        (String) map.get("exp"),
        (String) map.get("jti"),
        scopeList.toArray(new String[scopeList.size()]),
        (String) map.get("clientId")
    );

    // todo:
    //  return accessTokenDto = {"iss":"http://localhost:9001","sub":"9XE3-JI34-99999A","aud":["http://localhost:9002"],"iat":1653186276,"exp":1653186576,"jti":"7Pp80Asf","scope":["read","write","delete","openid","profile","email","address","phone"],"clientId":"TLkYmzi6sV9dG7SmXH6h5Y11jk5FvFkE"}
    this.logger.log("UserInfoRestController", "accessTokenDto = " + dto.toString());

    return dto;
  }
}
