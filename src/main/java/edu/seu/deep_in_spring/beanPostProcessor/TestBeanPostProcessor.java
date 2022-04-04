package edu.seu.deep_in_spring.beanPostProcessor;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;

public class TestBeanPostProcessor {
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        // 添加bean
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        context.registerBean("bean4", Bean4.class);

        // 调用该方法可以使@Autowired后处理器解析@Value获取String值
        context.getDefaultListableBeanFactory()
                .setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        /* 添加Bean后处理器 */
        // 解析@Autowired注解和@Value
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        // 解析@Resource, @PreDestroy, @PostConstruct注解
        context.registerBean(CommonAnnotationBeanPostProcessor.class);
        // 解析@ConfigurationProperties注解
        context.registerBean(ConfigurationPropertiesBindingPostProcessor.class);
        ConfigurationPropertiesBindingPostProcessor.register(context.getDefaultListableBeanFactory());

        // 执行容器内所有beanFactory后处理器
        context.refresh();
        System.out.println(((Bean1)context.getBean("bean1")).getPort()); // 123
        System.out.println(((Bean4)context.getBean("bean4")).getVersion()); // 1.8.0_312
        context.close();
    }
}