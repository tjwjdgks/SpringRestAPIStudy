package seo.study.springrestapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import seo.study.springrestapi.accounts.AccountService;

@Configuration
@EnableWebSecurity
// 자동 설정 적용 되지 않음, 커스터 마이징
public class SecurityConfig extends WebSecurityConfigurerAdapter  {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    // filter로
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 해당 인증 사용하지 않음
        web.ignoring().mvcMatchers("/docs/index.html");
        // 기본 정적 resource security 하지 않음
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    // security 안에서 http
    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 익명 요청 허용
        http
            .anonymous()
                .and()
            .formLogin()
                .and()
            .authorizeRequests()
                .mvcMatchers(HttpMethod.POST,"/api/**").authenticated()
                .anyRequest().authenticated();

    }

     */
}
