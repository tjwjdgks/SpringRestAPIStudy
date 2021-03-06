package seo.study.springrestapi.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 Spring Security에서 사용자의 정보를 담는 인터페이스이다.
 Spring Security에서 사용자의 정보를 불러오기 위해서 구현해야 하는 인터페이스
 */
@Service
public class AccountService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account){
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return this.accountRepository.save(account);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      Account account = accountRepository.findByEmail(username)
              .orElseThrow(()-> new UsernameNotFoundException(username));
        return new AccountAdpter(account);
    }

}
