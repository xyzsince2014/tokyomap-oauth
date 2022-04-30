package tokyomap.oauth.aspects;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // response 500 to clients
  public String handleException(Exception e) { // Exception is handled by this handler
    System.out.println(e.getStackTrace());
    return "/error/internalServerError.html";
  }
}
