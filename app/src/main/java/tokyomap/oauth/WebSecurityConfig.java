package tokyomap.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tokyomap.oauth.domain.services.authenticate.AuthenticateService;

@Configuration
@ComponentScan
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter { // todo: use SpringSecurity@5.7

  private final AuthenticateService authenticateService;
  private final String domain;

  @Autowired
  public WebSecurityConfig (AuthenticateService authenticateService, @Value("${domain.web}") String domain) {
    this.authenticateService = authenticateService;
    this.domain = domain;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/css/**", "/img/**", "/js/**").permitAll()
        .antMatchers("/api/**").permitAll()
        .antMatchers("/authenticate/**", "/sign-up").not().authenticated()
        .anyRequest().authenticated();

    http.formLogin()
        .loginPage("/authenticate")
        .loginProcessingUrl("/authenticate")
        .usernameParameter("email")
        .passwordParameter("password")
        .defaultSuccessUrl(domain + "/api/auth/authorise")
        .failureUrl("/authenticate?error=true");

    http.logout()
        .logoutUrl("/sign-out/pro")
        .deleteCookies("JSESSIONID");

    // todo: refine config
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .sessionFixation().newSession()
        .maximumSessions(1)
        .maxSessionsPreventsLogin(false);
//        .expiredSessionStrategy(new CustomSessionInformationExpiredStrategy());

    http.csrf().disable();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    try {
      auth.userDetailsService(this.authenticateService).passwordEncoder(passwordEncoder());
    } catch (UsernameNotFoundException e) {
      throw new Exception(e.getMessage());
    }
  }
}
