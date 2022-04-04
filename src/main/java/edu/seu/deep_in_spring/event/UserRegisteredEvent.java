package edu.seu.deep_in_spring.event;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * 自定义事件，继承ApplicationEvent
 */

public class UserRegisteredEvent extends ApplicationEvent {
    public UserRegisteredEvent(Object source) {
        super(source);
    }
}