package tokyomap.oauth.application.revoke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.domain.services.revoke.RevokeService;
import tokyomap.oauth.dtos.RevokeRequestDto;

@RestController
@RequestMapping("/revoke")
public class RevokeController {

  private final RevokeService revokeService;

  @Autowired
  public RevokeController(RevokeService revokeService) {
    this.revokeService = revokeService;
  }

  @RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded;charset=utf-8")
  @ResponseStatus(HttpStatus.NO_CONTENT) // return "204 No Content"
  public void revoke(RevokeRequestDto requestDto, @RequestHeader("Authorization") String authorization) {
    this.revokeService.execute(requestDto, authorization);
  }
}
