package tokyomap.oauth.application.miscellaneous;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequestMapping("/miscellaneous")
public class GreetingController {

  private final GreetingMessageSender greetingMessageSender;

  @Autowired
  public GreetingController(GreetingMessageSender greetingMessageSender) {
    this.greetingMessageSender = greetingMessageSender;
  }

  @RequestMapping(path = "/greeting", method = RequestMethod.GET)
  public SseEmitter greeting() throws IOException, InterruptedException {
    SseEmitter emitter = new SseEmitter();
    this.greetingMessageSender.send(emitter);
    return emitter;
  }
}
