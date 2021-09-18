package seo.study.springrestapi.events;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import seo.study.springrestapi.accounts.Account;
import seo.study.springrestapi.accounts.AccountRepository;
import seo.study.springrestapi.accounts.AccountRole;
import seo.study.springrestapi.accounts.AccountService;
import seo.study.springrestapi.common.BaseControllerTest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@ExtendWith(SpringExtension.class)
//@WebMvcTest
// BaseControllerTest 상속
class EventControllerTest extends BaseControllerTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors().withRequestDefaults(prettyPrint())
                        .and().operationPreprocessors().withResponseDefaults(prettyPrint()))
                .build();
    }

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
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links").exists());
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
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        requestFields(
                                fieldWithPath("id").description("Name of new Id"),
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("location").description("description of location"),
                                fieldWithPath("beginEnrollmentDateTime").description("description of beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("description of closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("description of beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("description of endEventDateTime"),
                                fieldWithPath("basePrice").description("description of basePrice"),
                                fieldWithPath("maxPrice").description("description of maxPrice"),
                                fieldWithPath("limitOfEnrollment").description("description of limitOfEnrollment"),
                                fieldWithPath("manager").description("description of manger"),
                                fieldWithPath("offline").description("description of offline"),
                                fieldWithPath("free").description("description of free"),
                                fieldWithPath("eventStatus").description("description of eventStatus")
                                ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        // relaxedResponseFields // 일부만 BODY 사용하는 것  // 문서 일부만 사용한 것 // 정확한 문서를 생성하지 못함
                        responseFields(
                                fieldWithPath("id").description("Name of new Id"),
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("location").description("description of location"),
                                fieldWithPath("beginEnrollmentDateTime").description("description of beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("description of closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("description of beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("description of endEventDateTime"),
                                fieldWithPath("basePrice").description("description of basePrice"),
                                fieldWithPath("maxPrice").description("description of maxPrice"),
                                fieldWithPath("limitOfEnrollment").description("description of limitOfEnrollment"),
                                fieldWithPath("offline").description("description of offline"),
                                fieldWithPath("free").description("description of free"),
                                fieldWithPath("eventStatus").description("description of eventStatus"),
                                fieldWithPath("manager").description("description of manger"),
                                fieldWithPath("_links.*").ignored(),
                                fieldWithPath("_links.self.*").ignored(),
                                fieldWithPath("_links.query-events.*").ignored(),
                                fieldWithPath("_links.update-event.*").ignored(),
                                fieldWithPath("_links.profile.*").ignored()
                        )
                ));

    }
    @Test
    @DisplayName("30개의 이벤트 10개씩 조회")
    public void eventPageTest() throws Exception {
        // Given
        IntStream.range(0,30).forEach(i->{
            this.generateEvent(i);
        });
        // When & Then
        // page는 0부터 시작
        this.mockMvc.perform(get("/api/events/page")
                        .param("page","1")
                        .param("size","10")
                        .param("sort","name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists());
    }
    @Test
    @DisplayName("기존의 이벤트 1개 조회하기")
    public void getEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);

        // When & Given
        this.mockMvc.perform(get("/api/events/{id}",event.getId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists());
    }
    @Test
    @DisplayName("기존의 이벤트 1개 조회하기 실패")
    public void getEventFail() throws Exception {

        // When & Given
        this.mockMvc.perform(get("/api/events/{id}",100))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("수정할 이벤트가 없는 경우")
    public void getChangeNoneEvent() throws Exception {
        EventDto dto = EventDto.builder()
                .name("update")
                .description("update test")
                .maxPrice(100)
                .basePrice(0)
                .build();
        String s = mapper.writeValueAsString(dto);
        this.mockMvc.perform(put("/api/events/{id}",100)
                        .contentType(MediaTypes.HAL_JSON)
                        .content(s))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("입력 데이터 바인딩 이상할 경우")
    public void getBindingErrorEvent() throws Exception{
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);
        eventDto.setBasePrice(-100);
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    @Test
    @DisplayName("도메인 로직 검증 실패")
    public void getVaildEvent() throws Exception{
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);
        eventDto.setBasePrice(30000);
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
         // given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists());

    }
    private Event generateEvent(int i) {
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
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .location("D2 startup")
                .build();

        Event save = this.eventRepository.save(event);
        return save;
    }

    // logic test
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