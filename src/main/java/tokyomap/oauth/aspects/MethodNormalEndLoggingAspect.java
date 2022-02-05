package tokyomap.oauth.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodNormalEndLoggingAspect {
  @AfterReturning("execution(* *..*Service.*(..))")
  public void endLog(JoinPoint joinPoint) {
    System.out.println("A method has ended: " + joinPoint.getSignature());
  }
}
