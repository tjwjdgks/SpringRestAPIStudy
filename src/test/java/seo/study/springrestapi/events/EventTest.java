package seo.study.springrestapi.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    public void builder(){
        Event event= Event.builder()
                .name("test")
                .description("real test")
                .build();

        assertThat(event).isNotNull();
    }
    @Test
    public void javaBean(){
        //Given
        String test = "test";

        //When
        Event event = new Event();
        event.setName(test);

        //Then
        assertThat(event.getName()).isEqualTo(test);
    }
}