package tokyomap.oauth.application.miscellaneous;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class GreetingMessageSender {
  @Async
  public void send(SseEmitter emitter) throws IOException, InterruptedException {
    // send an event every seconds
    emitter.send(emitter.event().id(UUID.randomUUID().toString()).data("Hello"));
    TimeUnit.SECONDS.sleep(1);
    emitter.send(emitter.event().id(UUID.randomUUID().toString()).data("hoge"));
    TimeUnit.SECONDS.sleep(1);
    emitter.send(emitter.event().id(UUID.randomUUID().toString()).data("fuga"));
    TimeUnit.SECONDS.sleep(1);
    emitter.send(emitter.event().id(UUID.randomUUID().toString()).data("Bye"));

    // complete async communication
    emitter.complete();
  }
}
