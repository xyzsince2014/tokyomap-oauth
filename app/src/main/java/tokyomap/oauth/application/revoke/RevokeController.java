package tokyomap.oauth.application.revoke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.dtos.RevokeRequestDto;

@RestController
@RequestMapping("/revoke")
public class RevokeController {

  private final RevokeApplicationService revokeApplicationService;

  @Autowired
  public RevokeController(RevokeApplicationService revokeApplicationService) {
    this.revokeApplicationService = revokeApplicationService;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  @ResponseStatus(HttpStatus.NO_CONTENT) // return "204 No Content"
  public void revoke(RevokeRequestDto requestDto, @RequestHeader("Authorization") String authorization) {
    this.revokeApplicationService.execute(requestDto, authorization);
  }
}
