package edu.seu.deep_in_spring.initialAndDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 测试三种初始化和销毁方法的执行顺序
 */
public class TestInitializeAndDestroy {
    public static void main(String[] args) {
        testInitialize();
    }

    /**
     * 测试三种不同的初始化时执行的方法：
     * 1. @PostConstruct 2. InitializingBean接口 3. @Bean 注解initMethod属性
     * 同理，三种销毁方法的执行顺序
     * 1. @PreDestroy 2. DisposableBean接口 3.@Bean 注解destroyMethod属性
     *
     * 拓展：Aware接口在初始化1和2之间执行
     */
    private static void testInitialize() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(CommonAnnotationBeanPostProcessor.class);
        context.registerBean("config", Config.class);
        context.refresh();
        context.close();
    }

    @Configuration
    static class Config {
        @Bean(initMethod = "init3")
        public Bean1 bean1() {
            return new Bean1();
        }
    }
}
