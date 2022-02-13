package tokyomap.oauth.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tokyomap.oauth.web.domain.services.user.ReservationUserDetailsService;

@Configuration
@ComponentScan
@EnableWebSecurity
@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private final ReservationUserDetailsService reservationUserDetailsService;

  @Autowired
  public WebSecurityConfig (ReservationUserDetailsService reservationUserDetailsService) {
    this.reservationUserDetailsService = reservationUserDetailsService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    //静的リソースに対するアクセスの場合はSpring Securityのフィルタを通過しないよう設定
    web.ignoring().antMatchers("/css/**", "/img/**", "/js/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests() // authorisation
          .antMatchers("/web/login").permitAll()
          .anyRequest().authenticated()
        .and().formLogin() // form authentication
          .loginPage("/web/login")
          .loginProcessingUrl("/web/authenticate") // endpoint to authenticate
          .usernameParameter("username")
          .passwordParameter("password")
          .defaultSuccessUrl("/web/rooms", true)
          .failureUrl("/web/login?error=true")
        .and().logout().logoutUrl("/web/logout").deleteCookies("JSESSIONID")
        .and().csrf().disable();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    try {
      authManagerBuilder.userDetailsService(this.reservationUserDetailsService).passwordEncoder(passwordEncoder());
    } catch (UsernameNotFoundException e) {
      throw new Exception(e.getMessage());
    }
  }
}
