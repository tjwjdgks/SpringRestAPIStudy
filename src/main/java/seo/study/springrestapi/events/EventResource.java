package seo.study.springrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

// object mapper가 bean serializer 사용한다
public class EventResource extends EntityModel<Event> {
    /*

    @JsonUnwrapped // Event 랩핑 하지 않음
    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

     */

    @Override
    public Event getContent() {
        return super.getContent();

    }
}
