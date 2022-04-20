package edu.seu.others.springProxy.env;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 切面类
 * 注入到spring容器中
 */
@Aspect
@Component
@Slf4j
public class MyAspect {

    /**
     * 增强所有方法
     */
    @Before("execution(* edu.seu.others.springProxy.env.Bean1.*(..))")
    public void before() {
        log.debug("before");
    }
}
