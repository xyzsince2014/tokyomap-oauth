package tokyomap.oauth.web.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodExceptionEndLoggingAspect {
  @AfterThrowing(value = "execution(* *..*Service.*(..))", throwing = "e")
  public void endLog(JoinPoint joinPoint, RuntimeException e) {
    System.out.println("A method has thrown an exception: " + joinPoint.getSignature());
    e.printStackTrace();
  }
}
