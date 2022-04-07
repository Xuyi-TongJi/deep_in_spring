package edu.seu.deep_in_spring.aop.dynamicAdvice;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class TestDynamicAdvice {

    @Aspect
    static class MyAspect {

        /**
         * 静态通知调用
         */
        @Before("execution(* foo(..))")
        public void before1() {
            System.out.println("before");
        }

        /**
         * 动态通知调用 -> 通知调用需要参数绑定[性能较低]
         * 执行时仍需要切点对象pointcut
         * @param x 通知参数
         */
        @Before("execution(* foo(..)) && args(x)")
        public void before2(int x) {
            System.out.printf("before2(%d)%n", x);
        }
    }

    static class Target {
        public void foo(int x) {
            System.out.printf("target foo(%d)%n", x);
        }
    }

    @Configuration
    static class MyConfig {

        /**
         * 解析@Aspect切面类的后处理器
         */
        @Bean
        AnnotationAwareAspectJAutoProxyCreator proxyCreator() {
            return new AnnotationAwareAspectJAutoProxyCreator();
        }

        @Bean
        public MyAspect myAspect() {
            return new MyAspect();
        }
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", MyConfig.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.refresh();

        AnnotationAwareAspectJAutoProxyCreator proxyCreator = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        Method method = AbstractAdvisorAutoProxyCreator.class.getDeclaredMethod("findEligibleAdvisors", Class.class, String.class);
        method.setAccessible(true);
        List<Advisor> advisors = (List<Advisor>)method.invoke(proxyCreator, Target.class, "target");

        Target target = new Target();

        // 创建代理对象
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisors(advisors);
        Object proxy = proxyFactory.getProxy(); // 获取代理对象

        // 适配为环绕通知
        List<Object> advices
                = proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(Target.class.getMethod("foo", int.class), Target.class);
        for (Object advice : advices) {
            System.out.println(advice);
        }
        // 动态通知会转换为以下通知: InterceptorAndDynamicMethodMatcher[它不是一个环绕通知]
        /*
            InterceptorAndDynamicMethodMatcher以成员变量形式组合了切点methodMatcher和通知methodInterceptor
         */
    }

}
