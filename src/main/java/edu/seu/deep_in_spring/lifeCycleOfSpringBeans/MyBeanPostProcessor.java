package edu.seu.deep_in_spring.lifeCycleOfSpringBeans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 自定义后处理器，实现两个PostProcessor子接口
 * Bean后处理器功能：在Bean的各个生命周期做功能增强
 */
@Slf4j
@Component
public class MyBeanPostProcessor implements InstantiationAwareBeanPostProcessor, DestructionAwareBeanPostProcessor {
    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (beanName.equals("springBeanLifeCycle")) {
            log.debug("销毁之前执行，如@PreDestory");
        }
    }

    /**
     * 实例化之前执行，即调用构造方法之前执行
     * 返回null不会替换原有的bean
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanName.equals("springBeanLifeCycle")) {
            log.debug("实例化之前执行，这里返回的对象会替换掉原本的bean");
        }
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (beanName.equals("springBeanLifeCycle")) {
            log.debug("实例化之后执行，这里如果返回false会跳过依赖注入阶段");
        }
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if (beanName.equals("springBeanLifeCycle")) {
            log.debug("@Autowired, @Value, @Resource等依赖注入阶段执行");
        }
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("springBeanLifeCycle")) {
            log.debug("初始化之前执行,如@PostConstruct @ConfigurationProperties，这里如果不返回null会替换掉原本的饿bean");
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("springBeanLifeCycle")) {
            log.debug("初始化之后执行，如果返回对象则会替换掉原本的bean，如代理增强");
        }
        return null;
    }
}
