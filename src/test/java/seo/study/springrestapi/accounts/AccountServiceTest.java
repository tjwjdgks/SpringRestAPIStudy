package seo.study.springrestapi.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {


    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername(){
        //Given
        String email = "seo@google.com";
        String password = "enter";
        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                .build();

        this.accountService.saveAccount(account);
        // When
        UserDetailsService userDetailsService =  accountService;
        UserDetails seo = userDetailsService.loadUserByUsername(email);

        // Then
        assertThat(this.passwordEncoder.matches(password,seo.getPassword())).isTrue();
    }
    @Test()
    public void findByUsernameFail(){
        /*
        방법 1
        assertThrows(UsernameNotFoundException.class,()->{
            String email = "random@email.com";
            accountService.loadUserByUsername(email);
        });

         */

        // 방법 2

        String email = "random@email.com";
        try{
            accountService.loadUserByUsername(email);
            fail("Supposed to be failed");
        }catch(UsernameNotFoundException e){
             assertThat(e.getMessage()).containsSequence(email);
        }


    }
}