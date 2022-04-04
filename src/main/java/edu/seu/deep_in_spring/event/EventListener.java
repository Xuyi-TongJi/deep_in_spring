package edu.seu.deep_in_spring.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventListener {

    @org.springframework.context.event.EventListener
    public void listen(UserRegisteredEvent event) {
        log.info("{}", event);
    }
}
