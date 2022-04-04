package edu.seu.deep_in_spring.initialAndDestroy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;

@Slf4j
public class Bean1 implements InitializingBean {

    @PostConstruct
    public void init() {
        log.debug("@PostConstruct初始化方法");
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("InitializingBean初始化方法");
    }

    public void init3() {
        log.debug("@Bean initMethod初始化方法");
    }
}
