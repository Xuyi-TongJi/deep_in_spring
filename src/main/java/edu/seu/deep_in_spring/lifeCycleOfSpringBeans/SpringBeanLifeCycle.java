package edu.seu.deep_in_spring.lifeCycleOfSpringBeans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 构造 --> 依赖注入 --> 初始化 --> 销毁
 */
@Slf4j
@Component("springBeanLifeCycle")
public class SpringBeanLifeCycle {

    public SpringBeanLifeCycle() {
        log.debug("构造Spring Bean");
    }

    /**
     * 字符串值注入
     */
    @Autowired
    public void autowire(TestBean testBean) {
        log.debug("依赖注入：{}", testBean.id);
    }

    @PostConstruct
    public void init() {
        log.debug("初始化");
    }

    @PreDestroy
    public void destroy() {
        log.debug("销毁");
    }
}

