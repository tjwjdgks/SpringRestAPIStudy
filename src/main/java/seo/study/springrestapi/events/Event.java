package seo.study.springrestapi.events;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *  java bean 스펙
 *  기본 생성자 있어야 하고 모든 필드에 대해서 Getter, Setter가 있어야 한다
 */
// lombok은 meta 애노테이션 지원 x
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 다른 연관관계가 있는  entity와 묶는 것은 좋지 않다
// 상호 참조 때문에 stack overflow 발생할 수도 있음
// Lombodk의 @Builder를 사용하다보면 기본값으로 null
@EqualsAndHashCode(of = "id")
@Entity
public class Event {
    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(value=EnumType.STRING) // 기본값 ORDINAL 순서에 따라서 0, 1, 2 숫자값 저장 ordinal 보다는 순서
    @Builder.Default
    private EventStatus eventStatus = EventStatus.DRAFT;

    public void updateFree() {
        if(this.basePrice == 0 && this.maxPrice == 0){
            this.free = true;
        }
        else{
            this.free = false;
        }
    }

    public void updateLocation() {
        if(this.location == null || this.location.isEmpty())
            this.offline = false;
        else
            this.offline = true;
    }
}
