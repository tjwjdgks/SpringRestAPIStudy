package seo.study.springrestapi.events;

import lombok.*;

import javax.persistence.Entity;
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
@EqualsAndHashCode(of = "id")
public class Event {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String loaction;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    private EventStatus eventStatus;
}
