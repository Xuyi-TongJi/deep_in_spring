package edu.seu.others.springProxy.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class Bean1 {
    public Bean2 bean2;
    public boolean initialized;

    public Bean2 getBean2() {
        return bean2;
    }

    @Autowired
    public void setBean2(Bean2 bean2) {
        log.debug("setBean2");
        this.bean2 = bean2;
    }

    @PostConstruct
    public void init() {
        log.debug("init");
        initialized = true;
    }
}
