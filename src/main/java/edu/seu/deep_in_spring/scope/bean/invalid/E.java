package edu.seu.deep_in_spring.scope.bean.invalid;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 三种实现单例(E)注入prototype(F,F2,F3)的方法：
 * 注入代理对象，注入工厂对象或者直接注入ApplicationContext容器
 * 建议使用后二者
 */
@Component
public class E {

    /**
     * 使用@Lazy注入prototypeBean F
     * 真正注入的是SpringCGLIB代理对象
     */
    @Lazy
    @Autowired
    private F f;

    /**
     * 使用对象工厂注入protoTypeBean F2
     * 真正注入的是对象工厂
     */
    @Autowired
    private ObjectFactory<F2> factory;

    /**
     * 注入ApplicationContext容器，以其作为prototypeBean的工厂，生产prototypeBean
     */
    @Autowired
    private ApplicationContext context;

    public F getF() {
        return f;
    }

    public F2 getF2() {
        return factory.getObject();
    }

    /**
     * 通过ApplicationContext getBean，能够正确识别@Scope("prototype")，生产prototype对象
     */
    public F3 getF3() {
        return context.getBean(F3.class);
    }
}