package seo.study.springrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@ExtendWith(SpringExtension.class)
//@WebMvcTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class EventControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    /*
    Autowired하면 application context에 MockBean 객체가 들어간다
    @MockBean
    EventRepository eventRepository;
     */

    @Test
    public void createEvent() throws Exception {

        Event event = Event.builder()
                .name("Spring")
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2020,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2020,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("D2 startup")
                .build();
        event.setId(10);
        //Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists());
    }
    /**
     * 입력값 제한하는 방법
     * 컨트롤러에서 ResponseBody로 객체를 받기 때문에 특정 정보(가격, 위치 등)이 사용자가 임의적으로 입력시 들어올수 있다. 하지만 이런 정보들은
     * 사용자가 접근하게 하지 말아야 하는 정보이므로 입력값이 들어 왔을 때 제어를 해야한다 (무시하거나 예외 처리하거나)
     */
    // 무시하는 경우, 유연한 프로그래밍
    /*
    @Test
    public void createEventIgnoreInput() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2020,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2020,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .free(true)
                .loaction("D2 startup")
                .build();

        // null point exception 발생
        // save에 들어가는 객체와 실제 event 객체가 다르기 때문에(eventDto로 부터 event객체를 만들었음)
        // Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/ignore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("basePrice").value(Matchers.not(true)))
                .andExpect(jsonPath("id").exists());
    }

     */
    // 예외처리하는 경우, 오해없는 프로그래밍
    @Test
    public void createEventExceptionInput() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2020,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2020,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .free(true)
                .location("D2 startup")
                .build();

        mockMvc.perform(post("/api/events/badrequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 없음")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events/badrequest")
                        .contentType(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());

    }
    // validator로 값 검증
    @Test
    @DisplayName("입력값이 이상함")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,11,25,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2020,11,27,14,21))
                .endEventDateTime(LocalDateTime.of(2020,11,26,14,21))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .loaction("D2 startup")
                .build();

        mockMvc.perform(post("/api/events/badrequest")
                        .contentType(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());

    }
    @Test
    @DisplayName("입력값이 이상함 응답메시지에 출력")
    public void createEvent_Bad_Request_Wrong_InputBody() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,11,25,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2020,11,27,14,21))
                .endEventDateTime(LocalDateTime.of(2020,11,26,14,21))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .loaction("D2 startup")
                .build();

        mockMvc.perform(post("/api/events/badrequest")
                        .contentType(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists());
    }
    @Test
    @DisplayName("비즈니스 로직 테스트")
    public void createEvent_Test_Input() throws Exception {
         Event event= Event.builder()
                .name("Spring")
                .description("rest api")
                .beginEnrollmentDateTime(LocalDateTime.of(2020,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2020,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2020,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("D2 startup")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @Test
    public void isFree(){
        //Given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();
        //When
        event.updateFree();
        //Then
        assertThat(event.isFree()).isTrue();

        //Given
        Event event_NotFree = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();
        //When
        event.updateFree();
        //Then
        assertThat(event_NotFree.isFree()).isFalse();
    }
    @Test
    public void testOffline(){
        //Given
        Event event = Event.builder()
                .location("강남")
                .build();
        //When
        event.updateLocation();
        // then
        assertThat(event.isOffline()).isTrue();

        //Given
        Event event2 = Event.builder().build();
        //When
        event2.updateLocation();
        // then
        assertThat(event2.isOffline()).isFalse();

    }
    // refactoring

    @ParameterizedTest
    @MethodSource("paramIsFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree){
        // given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // when
        event.updateFree();
        // then
        assertThat(event.isFree()).isEqualTo(isFree);

    }
    public static Stream<Arguments> paramIsFree(){
        return Stream.of(
                Arguments.of(0,0,true),
                Arguments.of(100, 0, false),
                Arguments.of(0, 100, false),
                Arguments.of(100, 200, false)
        );
    }
}