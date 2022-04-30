package tokyomap.oauth.interceptors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginHandlerInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
    this.logging("preHandle()");
    return true; // revoke the handler
    // return false; // abort calling the hanlder
  }

  public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
    this.logging("postHandle(): the handler has normally completed.");
  }

  public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
    this.logging("afterCompletion(): the handler has normally completed or thrown an exception.");
  }

  private void logging(String message) throws IOException {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    try (FileOutputStream fos = new FileOutputStream("/usr/share/tomcat/logs/application." + sdf.format(date) + ".log", true)){
      System.setOut(new PrintStream(fos));
      SimpleDateFormat sdfForLog = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
      System.out.println(sdfForLog.format(date) + " INFO [LoginHandlerInterceptor] " + message);

    } catch(FileNotFoundException e) {
      e.printStackTrace();
      throw e;
    }
  }
}
