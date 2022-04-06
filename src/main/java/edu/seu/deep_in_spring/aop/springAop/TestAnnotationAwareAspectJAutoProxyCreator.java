package edu.seu.deep_in_spring.aop.springAop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 模拟实现AnnotationAwareAspectJAutoProxyCreator的核心方法
 */
public class TestAnnotationAwareAspectJAutoProxyCreator {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean("aspect1", Aspect1.class);
        context.registerBean("config123", Config123.class);
        context.refresh();
        AnnotationAwareAspectJAutoProxyCreator proxyCreator
                = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        // findEligibleAdvisors方法，可以判断目标类在context中有没有合适的切面
        Method method
                = AbstractAdvisorAutoProxyCreator.class.getDeclaredMethod("findEligibleAdvisors", Class.class, String.class);
        method.setAccessible(true);

        // 如果不返回空，则说明需要为这个类创建代理类
        List<Advisor> advisors = (List<Advisor>)method.invoke(proxyCreator, Target2.class, "target2");

        // wrapIfNecessary方法, 如果context中有适合目标类的advisor,则创建相应的代理类
        Method method1 = AbstractAutoProxyCreator.class
                .getDeclaredMethod("wrapIfNecessary", Object.class, String.class, Object.class);
        method1.setAccessible(true);
        Target1 t1 = new Target1();
        Target2 t2 = new Target2();
        Object o1 = method1.invoke(proxyCreator, t1, "target1", "target1"); // cglib
        System.out.println(o1.getClass());
        Object o2 = method1.invoke(proxyCreator, t2, "target2", "target2"); // cglib
        System.out.println(o2.getClass());

          // 代理对象调用原始方法：实现增强
        ((Target1)o1).foo();

        context.close();

    }

    static class Target1 {
        public void foo() {
            System.out.println("target1 foo");
        }
    }

    static class Target2 {
        public void bar() {
            System.out.println("target2 bar");
        }
    }

    /**
     * 一个Aspect切面中有多组通知+切点
     */
    @Aspect
    static class Aspect1 {
        @Before("execution(* foo())")
        public void before() {
            System.out.println("aspect before");
        }

        @After("execution(* bar())")
        public void after() {
            System.out.println("aspect bar");
        }
    }

    /**
     * 用配置类实现一个Advisor[一个切点和一个通知组成]
     */
    @Configuration
    static class Config123 {
        @Bean
        public Advisor advisor(MethodInterceptor advice) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            return new DefaultPointcutAdvisor(pointcut, advice);
        }

        @Bean
        public MethodInterceptor advisor() {
            return (invocation -> {
                System.out.println("before");
                Object proceed = invocation.proceed();
                System.out.println("after");
                return proceed;
            });
        }
    }
}