package seo.study.springrestapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import seo.study.springrestapi.accounts.Account;
import seo.study.springrestapi.accounts.AccountAdpter;
import seo.study.springrestapi.commons.ErrorsResource;
import seo.study.springrestapi.index.IndexController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events",produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {


    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventVaildator eventVaildator;

    @GetMapping
    @ResponseBody
    public String getTest(){
        return "Hello spring api";
    }
    @PostMapping
    public ResponseEntity createEntity(@RequestBody Event event){
        event.updateFree();
        event.updateLocation();
        event.setEventStatus(EventStatus.DRAFT);
        Event newEvent = eventRepository.save(event);

        WebMvcLinkBuilder selfLink = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLink.toUri();

        EntityModel<Event> body = EventResource.of(event);
        body.add(linkTo(EventController.class).withRel("query-events"));
        body.add(selfLink.withSelfRel());
        body.add(selfLink.withRel("update-event"));
        body.add(Link.of("/docs/index.html").withRel("profile"));
        return ResponseEntity.created(createUri).body(body);


    }

    // #spring.jackson.deserialization.fail-on-unknown-properties=false 설정해야 주석된 test 통과
    @PostMapping("/ignore")
    public ResponseEntity createEntity(@RequestBody EventDto eventDto){
        Event event = modelMapper.map(eventDto,Event.class);
        event.updateFree();
        event.updateLocation();
        Event newEvent = eventRepository.save(event);
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();

        return ResponseEntity.created(createUri).body(newEvent);
    }
    @PostMapping("/badrequest")
    public ResponseEntity createBadEntity(@RequestBody @Valid EventDto eventDto, Errors error){

        if(error.hasErrors()){
            EntityModel<Errors> errors = ErrorsResource.of(error, linkTo(methodOn(IndexController.class).index()).withRel("index"));
            return ResponseEntity.badRequest().body(errors);
        }

        eventVaildator.vaildate(eventDto,error);
        if(error.hasErrors()){
            EntityModel<Errors> errors = ErrorsResource.of(error, linkTo(methodOn(IndexController.class).index()).withRel("index"));
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto,Event.class);
        event.updateFree();
        event.updateLocation();
        Event newEvent = eventRepository.save(event);
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();

        return ResponseEntity.created(createUri).body(newEvent);
    }

    @GetMapping("/page")
    //  @AuthenticationPrincipal getPrincipal로 return 받을 수 있는 객체를 바로 받을 수 있음
    public ResponseEntity pageQueryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler,
                                          @AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account") Account account){
        // authentication 정보
        /*
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
         */

        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> entityModels = assembler.toModel(page,
                e->EventResource.of(e).add(linkTo(EventController.class).slash(e.getId()).withSelfRel()));
        if(account != null){
            entityModels.add(linkTo(EventController.class).withRel("create-event"));

        }
        return ResponseEntity.ok().body(entityModels);

     }
     @GetMapping("/{id}")
    public ResponseEntity getIdEvents(@PathVariable Integer id){
         Optional<Event> byId = this.eventRepository.findById(id);
         if(!byId.isPresent()){
             return ResponseEntity.notFound().build();
         }
         Event event = byId.get();
         return ResponseEntity.ok(EntityModel.of(event).add(linkTo(EventController.class).slash(event.getId()).withSelfRel()));
     }
     @PutMapping("/{id}")
    public ResponseEntity chageEvents(@PathVariable Integer id, @RequestBody @Valid EventDto body, Errors errors){
         Optional<Event> byId = this.eventRepository.findById(id);
         if(byId.isEmpty()){
             return ResponseEntity.notFound().build();
         }
         if(errors.hasErrors()){
             return ResponseEntity.badRequest().build();
         }
         eventVaildator.vaildate(body,errors);
         if(errors.hasErrors())
             return ResponseEntity.badRequest().build();

         Event event = byId.get();
         // body 에서 event로 옮겨준다
         this.modelMapper.map(body,event);
         this.eventRepository.save(event);
         return ResponseEntity.ok(EntityModel.of(event).add(linkTo(EventController.class).slash(event.getId()).withSelfRel()));
     }
}
