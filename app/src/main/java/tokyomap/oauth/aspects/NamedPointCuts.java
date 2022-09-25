package tokyomap.oauth.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NamedPointCuts {
  @Pointcut("execution(* tokyomap.oauth.application.*.*Controller.*(..))")
  public void anyControllerOperation() {}

  @Pointcut("execution(* tokyomap.oauth.domain.services.*.*Service.*(..))")
  public void anyServiceOperation() {}
}
