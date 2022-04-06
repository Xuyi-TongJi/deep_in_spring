package edu.seu.deep_in_spring.aop.springAop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Arrays;

public class DiffAdvisorAspect {
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("aspect1", Aspect1.class);
        context.registerBean("config", Config.class);
        // resolve @Bean
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);
        /*
            AnnotationAwareAspectJAutoProxyCreator的功能扩展时机：
            Bean创建 -> (*)依赖注入 -> 初始化(*)

         */
        context.refresh();
        /*
            Spring ApplicationContext解析切面流程-> 基于AnnotationAwareAspectJAutoProxyCreator后处理器
            1. 找到容器中所有切面Advisor [高级切面转换为低级切面]
            2. 根据切面创建代理对象
         */
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
    static class Config {
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
