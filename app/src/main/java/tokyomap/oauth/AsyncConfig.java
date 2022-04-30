package tokyomap.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync // enable async communications by `@Async`
public class AsyncConfig {

  /**
   * configure TaskExecutor used by `@Async`.
   * `@Async` uses SimpleAsyncTaskExecutor by default, that creates a new thread for each request
   * @return
   */
  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(5);
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.setQueueCapacity(25);
    return taskExecutor;
  }
}
