package tokyomap.oauth.filters;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

public class ClinentInfoMdcPutFilter extends OncePerRequestFilter {
  private static final String FORWARDED_FOR_HEADER_NAME = "X-Forwarded-For";

  private String mdcKey = this.FORWARDED_FOR_HEADER_NAME;

  public ClinentInfoMdcPutFilter() {}

  public ClinentInfoMdcPutFilter(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  protected final void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
    String remoteIp = Optional.ofNullable(req.getHeader(this.FORWARDED_FOR_HEADER_NAME)).orElse(req.getRemoteAddr());
    MDC.put(mdcKey, remoteIp);
    try {
      filterChain.doFilter(req, res);
    } catch (Exception e) {
      e.getStackTrace();
    } finally {
      MDC.remove(mdcKey);
    }
  }

  public String getMdcKey() {
    return mdcKey;
  }

  public void setMdcKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }
}
