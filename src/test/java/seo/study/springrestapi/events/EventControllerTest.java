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
import org.springframework.security.oauth2.common.util.JsonParser;
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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@ExtendWith(SpringExtension.class)
//@WebMvcTest
// BaseControllerTest ??????
class EventControllerTest extends BaseControllerTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors().withRequestDefaults(prettyPrint())
                        .and().operationPreprocessors().withResponseDefaults(prettyPrint()))
                .build();

        // test??? in memory db, test?????? in memory db ??????
        // ???????????? ??????????????? key?????? ??????????????? ???????????? ???????????????
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }
    // ?????? ??????
    public String getAccessToken() throws Exception{
        // Given
        String email = "seo@email.com";
        String password = "seo";
        Account seo = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(seo);

        String clientId = "myApp";
        String clientSecret = "pass";

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", email)
                .param("password", password)
                .param("grant_type", "password"));
        MockHttpServletResponse response = perform.andReturn().getResponse();
        String contentAsString = response.getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(contentAsString).get("access_token").toString();
    }
    /*
    Autowired?????? application context??? MockBean ????????? ????????????
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
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists());
    }

    /**
     * ????????? ???????????? ??????
     * ?????????????????? ResponseBody??? ????????? ?????? ????????? ?????? ??????(??????, ?????? ???)??? ???????????? ??????????????? ????????? ???????????? ??????. ????????? ?????? ????????????
     * ???????????? ???????????? ?????? ????????? ?????? ??????????????? ???????????? ?????? ?????? ??? ????????? ???????????? (??????????????? ?????? ???????????????)
     */
    // ???????????? ??????, ????????? ???????????????
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

        // null point exception ??????
        // save??? ???????????? ????????? ?????? event ????????? ????????? ?????????(eventDto??? ?????? event????????? ????????????)
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
    // ?????????????????? ??????, ???????????? ???????????????
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
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("???????????? ??????")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events/badrequest")
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
                        .contentType(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());

    }
    // validator??? ??? ??????
    @Test
    @DisplayName("???????????? ?????????")
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
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
                        .contentType(MediaTypes.HAL_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());

    }
    @Test
    @DisplayName("???????????? ????????? ?????????????????? ??????")
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
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
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
    @DisplayName("???????????? ?????? ?????????")
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
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
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
                        // relaxedResponseFields // ????????? BODY ???????????? ???  // ?????? ????????? ????????? ??? // ????????? ????????? ???????????? ??????
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
    @DisplayName("30?????? ????????? 10?????? ??????")
    public void eventPageTest() throws Exception {
        // Given
        IntStream.range(0,30).forEach(i->{
            this.generateEvent(i);
        });
        // When & Then
        // page??? 0?????? ??????
        this.mockMvc.perform(get("/api/events/page")
                        .param("page","1")
                        .param("size","10")
                        .param("sort","name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists());
    }

    @Test
    @DisplayName("????????? ????????? 1??? ????????????")
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
    @DisplayName("????????? ????????? 1??? ???????????? ??????")
    public void getEventFail() throws Exception {

        // When & Given
        this.mockMvc.perform(get("/api/events/{id}",100))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("????????? ???????????? ?????? ??????")
    public void getChangeNoneEvent() throws Exception {
        EventDto dto = EventDto.builder()
                .name("update")
                .description("update test")
                .maxPrice(100)
                .basePrice(0)
                .build();
        String s = mapper.writeValueAsString(dto);
        this.mockMvc.perform(put("/api/events/{id}",100)
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
                        .contentType(MediaTypes.HAL_JSON)
                        .content(s))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("?????? ????????? ????????? ????????? ??????")
    public void getBindingErrorEvent() throws Exception{
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);
        eventDto.setBasePrice(-100);
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    @Test
    @DisplayName("????????? ?????? ?????? ??????")
    public void getVaildEvent() throws Exception{
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);
        eventDto.setBasePrice(30000);
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    @Test
    @DisplayName("???????????? ??????????????? ????????????")
    public void updateEvent() throws Exception {
         // given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                        .header(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
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
                .location("??????")
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