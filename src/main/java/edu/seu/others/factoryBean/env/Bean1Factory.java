package edu.seu.others.factoryBean.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * Bean1工厂对象
 */
@Component("bean1")
@Slf4j
public class Bean1Factory implements FactoryBean<Bean1> {
    @Override
    public Bean1 getObject() throws Exception {
        Bean1 bean1 = new Bean1();
        log.debug("create bean1");
        return bean1;
    }

    /**
     * 决定spring容器根据类型获取Bean或springBean依赖注入能否成功
     */
    @Override
    public Class<?> getObjectType() {
        return Bean1.class;
    }

    /**
     * 该工厂生产的对象是否单例
     * 决定了getObject()方法调用一次还是多次
     */
    @Override
    public boolean isSingleton() {
        return true;
    }
}
