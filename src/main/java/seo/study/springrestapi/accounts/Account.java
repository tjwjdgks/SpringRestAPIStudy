package seo.study.springrestapi.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    private Integer id;

    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)  // 여러개의 enum을 가질 수 있는 것이므로 // 기본으로 set과 list fetch mode lazy // but 현재의 enum은 갯수가 작으므로 EAGER
    @Enumerated(value = EnumType.STRING)
    private Set<AccountRole> roles;
}
