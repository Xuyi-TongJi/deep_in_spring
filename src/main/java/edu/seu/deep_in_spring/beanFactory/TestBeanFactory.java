package edu.seu.deep_in_spring.beanFactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

public class TestBeanFactory {
    public static void main(String[] args) {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        // bean 的定义 BeanFactory根据bean的定义创建对象 --> class scope[singleton, prototype], 初始化，销毁

        // 构造Bean
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(Config.class).setScope("singleton").getBeanDefinition();
        // 注册Bean [name, beanDefinition]
        factory.registerBeanDefinition("config", beanDefinition);

        // 此时factory中只有一个bean --> beanFactory不能解析@Configuration和@Bean注解
        /*for (String name : factory.getBeanDefinitionNames()) {
            System.out.println(name);
        }*/
        // 为factory添加后处理器[]
        /*
            beanFactory后处理器：补充了一些Bean的定义，可以解析@Configuation @Component @Bean等注解
            bean后处理器，针对bean的生命周期的各个阶段提供扩展，可以解析@Autowired @Resource ...
        */
        AnnotationConfigUtils.registerAnnotationConfigProcessors(factory);

        // 运行Bean工厂后处理器，解析@Configuration和@Bean注解
        Map<String, BeanFactoryPostProcessor> factoryPostProcessorMap = factory.getBeansOfType(BeanFactoryPostProcessor.class);
        factoryPostProcessorMap.values().forEach(
                (beanFactoryPostProcessor -> {
                    beanFactoryPostProcessor.postProcessBeanFactory(factory);
                })
        );
        // 运行bean后处理器，解析@Autowired注解
        Map<String, BeanPostProcessor> postProcessorMap = factory.getBeansOfType(BeanPostProcessor.class);
        postProcessorMap.values().forEach(
                factory::addBeanPostProcessor
        );
        for (String name : factory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println(factory.getBean(Bean1.class).getBean2());

        // 对于singleton的Bean，beanFactory默认采用懒惰初始化，如果需要在JVM进程创建时预先创建对象，则调用以下方法
        // 预先创建所有单例对象
        factory.preInstantiateSingletons();

    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    @Slf4j
    static class Bean1 {
        public Bean1() {
            log.info("构造Bean1");
        }

        @Autowired
        private Bean2 bean2;

        public Bean2 getBean2() {
            return bean2;
        }
    }

    @Slf4j
    static class Bean2 {
        public Bean2() {
            log.info("构造Bean2");
        }
    }
}
