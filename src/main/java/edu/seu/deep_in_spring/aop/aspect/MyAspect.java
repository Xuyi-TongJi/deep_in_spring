package edu.seu.deep_in_spring.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Aspect
 */
@Aspect
@Slf4j
public class MyAspect {

    /**
     * 前置通知
     */
    @Before("execution(* edu.seu.deep_in_spring.aop.service.MyService.*())")
    public void before() {
        log.info("before()");
    }
}
