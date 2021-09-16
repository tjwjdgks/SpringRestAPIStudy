package seo.study.springrestapi.events;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Dto를 통해서 Entity의 값을 변경할 부분만 추출한다.
 * 단점은 Entity의 변경할 수 있는 필드 값을 가지고 있으므로 중복이 생긴다
 */
@Builder
@Data // entity가 아니므로 data 가능
@NoArgsConstructor
@AllArgsConstructor
// validation 수행
public class EventDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private LocalDateTime beginEnrollmentDateTime;
    @NotNull
    private LocalDateTime closeEnrollmentDateTime;
    @NotNull
    private LocalDateTime beginEventDateTime;
    @NotNull
    private LocalDateTime endEventDateTime;
    private String loaction;
    private boolean offline;
    @Min(0)
    private int basePrice;
    @Min(0)
    private int maxPrice;
    @Min(0)
    private int limitOfEnrollment;
}
