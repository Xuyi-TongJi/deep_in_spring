package edu.seu.deep_in_spring.aop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyService {

    public void foo() {
        bar();
        log.info("foo()");
    }

    public void bar() {
        log.info("bar()");
    }
}