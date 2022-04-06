package edu.seu.deep_in_spring.aop.springAop;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import javax.annotation.PostConstruct;

/**
 * 测试Spring代理类创建的时机
 */
public class SpringTimeToCreateProxy {
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean("config", Config.class);
        context.refresh();
        Bean1 bean = context.getBean(Bean1.class);
        bean.foo();

        /*
            Bean1 is instantiation[实例化]
            Bean1 is initializing
            // Bean1 Proxy 被创建[Spring Bean Initializing之后]
            23:41:55.646 [main] DEBUG org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating shared instance of singleton bean 'bean2'
            Bean2 is instantiation
            // @Autowired注入代理对象
            class edu.seu.deep_in_spring.aop.springAop.SpringTimeToCreateProxy$Bean1$$EnhancerBySpringCGLIB$$2f73baa3is Set
            Bean2 is initializing
            before
            after
            [代理的创建时机，在不存在循环依赖时，为初始化之后]
            [依赖注入方法和初始化方法不应该被增强，应该在原始对象上被调用]
         */
    }

    @Configuration
    static class Config {

        /**
         * 解析AspectJ并创建代理对象的后处理器
         */
        @Bean
        public AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator() {
            return new AnnotationAwareAspectJAutoProxyCreator();
        }

        /**
         * 解析@Autowired注解，实现自动注入
         */
        @Bean
        public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
            return new AutowiredAnnotationBeanPostProcessor();
        }

        /**
         * 解析PostConstruct注解的后处理器
         */
        @Bean
        public CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor() {
            return new CommonAnnotationBeanPostProcessor();
        }

        /**
         * 细粒度切面
         */
        @Bean
        public Advisor advisor(MethodInterceptor advice) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            return new DefaultPointcutAdvisor(pointcut, advice);
        }

        /**
         * 通知[方法增强逻辑]
         */
        @Bean
        public MethodInterceptor advice() {
            return (invocation -> {
                System.out.println("before");
                Object proceed = invocation.proceed();
                System.out.println("after");
                return proceed;
            });
        }

        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean1 {
        public void foo() {
            System.out.println("Bean1 foo~");
        }

        public Bean1() {
            System.out.println("Bean1 is instantiation[实例化]");
        }

        /**
         * Spring Bean初始化时执行该逻辑
         */
        @PostConstruct
        public void init() {
            System.out.println("Bean1 is initializing");
        }
    }

    static class Bean2 {
        public Bean2() {
            System.out.println("Bean2 is instantiation");
        }

        /**
         * 被注入的是代理对象
         */
        @Autowired
        public void setBean1(Bean1 bean1) {
            System.out.println(bean1.getClass() + "is Set");
        }

        @PostConstruct
        public void init() {
            System.out.println("Bean2 is initializing");
        }
    }
}
