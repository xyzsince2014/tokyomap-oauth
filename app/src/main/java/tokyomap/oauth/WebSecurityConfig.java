package tokyomap.oauth;

import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public WebSecurityConfig (AuthenticateService authenticateService) {
    this.authenticateService = authenticateService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/css/**", "/img/**", "/js/**").permitAll()
        .antMatchers("/authenticate/**").not().authenticated()
        // todo: .antMatchers("/api/**").permitAll()
        .antMatchers("/introspect/**").permitAll()
        .antMatchers("/public-keys/**").permitAll()
        .antMatchers("/register/**").permitAll()
        .antMatchers("/revoke/**").permitAll()
        .antMatchers("/token/**").permitAll()
        .anyRequest().authenticated();

    http.formLogin()
        .loginPage("/authenticate/pre")
        .loginProcessingUrl("/authenticate/pro")
        .usernameParameter("username")
        .passwordParameter("password")
        .failureUrl("/authenticate/pre?error=true");

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
