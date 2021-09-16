package seo.study.springrestapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events",produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {


    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventVaildator eventVaildator;
    @PostMapping
    public ResponseEntity createEntity(@RequestBody Event event){
        event.updateFree();
        event.updateLocation();
        event.setEventStatus(EventStatus.DRAFT);
        Event newEvent = eventRepository.save(event);

        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();

        return ResponseEntity.created(createUri).body(newEvent);
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

        if(error.hasErrors())
            return ResponseEntity.badRequest().build();

        eventVaildator.vaildate(eventDto,error);
        if(error.hasErrors())
            return ResponseEntity.badRequest().body(error);

        Event event = modelMapper.map(eventDto,Event.class);
        event.updateFree();
        event.updateLocation();
        Event newEvent = eventRepository.save(event);
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();

        return ResponseEntity.created(createUri).body(newEvent);
    }
}
