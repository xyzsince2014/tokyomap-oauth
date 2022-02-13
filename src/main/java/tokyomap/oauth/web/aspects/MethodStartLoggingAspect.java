package tokyomap.oauth.web.aspects;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.text.SimpleDateFormat;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodStartLoggingAspect {

  /**
   * a Before Advice
   * @param joinPoint
   */
  @Before("execution(* tokyomap.oauth.web.domain.services.*.*Service.*(..))")
  public void startLog(JoinPoint joinPoint) throws IOException {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    try (FileOutputStream fos = new FileOutputStream("/usr/share/tomcat/logs/application." + sdf.format(date) + ".log", true)){
      System.setOut(new PrintStream(fos));
      SimpleDateFormat sdfForLog = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
      System.out.println(sdfForLog.format(date) + " INFO [service] A method has started: " + joinPoint.getSignature());
    } catch(FileNotFoundException e) {
      e.printStackTrace();
    }
  }

}
