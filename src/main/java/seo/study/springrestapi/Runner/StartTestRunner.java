package seo.study.springrestapi.Runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import seo.study.springrestapi.accounts.Account;
import seo.study.springrestapi.accounts.AccountRole;
import seo.study.springrestapi.accounts.AccountService;
import seo.study.springrestapi.config.AppProperties;

import java.util.Set;

@Component
public class StartTestRunner implements ApplicationRunner {

    @Autowired
    AccountService accountService;
    @Autowired
    AppProperties appProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account admin = Account.builder()
                .email(appProperties.getAdminUsername())
                .password(appProperties.getAdminPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(admin);

        Account user = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.USER))
                .build();
        accountService.saveAccount(user);
    }
}
