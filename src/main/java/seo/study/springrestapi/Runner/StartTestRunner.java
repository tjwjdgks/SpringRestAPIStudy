package seo.study.springrestapi.Runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import seo.study.springrestapi.accounts.Account;
import seo.study.springrestapi.accounts.AccountRole;
import seo.study.springrestapi.accounts.AccountService;

import java.util.Set;

@Component
public class StartTestRunner implements ApplicationRunner {

    @Autowired
    AccountService accountService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account seo = Account.builder()
                .email("seo@naver.com")
                .password("seo")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(seo);
    }
}
