package edu.seu.deep_in_spring.aware;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 执行顺序：先回调执行Aware接口，再执行InitializingBean接口的afterPropertiesSet方法
 */
@Slf4j
public class MyBean2 implements ApplicationContextAware, InitializingBean {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.debug("当前Bean" + this  + "的容器是" + applicationContext.getApplicationName());
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("当前Bean" + this + "要执行的初始化操作");
    }
}
