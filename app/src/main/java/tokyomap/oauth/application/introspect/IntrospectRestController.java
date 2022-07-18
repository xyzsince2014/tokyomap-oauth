package tokyomap.oauth.application.introspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.domain.services.introspect.IntrospectService;
import tokyomap.oauth.domain.services.ApiException;
import tokyomap.oauth.dtos.IntrospectResponseDto;
import tokyomap.oauth.dtos.RequestIntrospectDto;

@RestController
@RequestMapping("/introspect")
public class IntrospectRestController {

  private final IntrospectService introspectService;

  @Autowired
  public IntrospectRestController(IntrospectService introspectService) {
    this.introspectService = introspectService;
  }

  @RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded;charset=utf-8")
  public ResponseEntity<IntrospectResponseDto> introspect(RequestIntrospectDto requestDto, @RequestHeader("Authorization") String authorization) {

    try {
      Boolean isActive = this.introspectService.execute(requestDto.getToken(), authorization);
      return ResponseEntity.status(HttpStatus.OK).body(new IntrospectResponseDto(isActive));

    } catch (ApiException e) {
      IntrospectResponseDto responseDto = new IntrospectResponseDto(e.getErrorMessage(), false);
      return ResponseEntity.status(e.getStatusCode()).body(responseDto);

    } catch (Exception e) {
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
