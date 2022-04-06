package edu.seu.deep_in_spring.aop.springAop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 高级切面转换为低级切面
 * 转换逻辑 -> 后处理器对@Aspect进行注解解析，并解析为细粒度的Advisor
 */
public class AspectToAdvisor {

    /**
     * 切面类@Aspect, 包含多组Advisor切面
     */
    static class Aspect {

        @Before("execution(* foo())")
        public void before1() {
            System.out.println("before1");
        }

        @Before("execution(* foo())")
        public void before2() {
            System.out.println("before2");
        }

        public void after() {
            System.out.println("after");
        }

        public void afterReturning() {
            System.out.println("afterReturning");
        }

        public void afterThrowing() {
            System.out.println("afterThrowing");
        }

        public Object around(ProceedingJoinPoint pjp) throws Throwable {
            return pjp.proceed();
        }
    }

    static class Target {
        public void foo() {
            System.out.println("target foo");
        }
    }

    /**
     * 解析@Aspect注解并将其转换为Advisor的过程
     */
    private static List<Advisor> testBuildAdvisors() {
        // 最终创建的切面集合
        List<Advisor> list = new ArrayList<>();

        // 创建单例切面工厂，代表切面类Aspect是单例的
        SingletonAspectInstanceFactory factory = new SingletonAspectInstanceFactory(new Aspect());
        for (Method method : Aspect.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                // 解析@Before

                // 切点表达式 设置切点
                String execution = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(execution);

                // 设置通知 args: 增强逻辑[method]，切点表达式, 切面实例工厂
                AspectJMethodBeforeAdvice advice = new AspectJMethodBeforeAdvice(method, pointcut, factory);

                // 切点 + 通知 -> advisor
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
        }
        return list;
    }

    public static void main(String[] args) {
        // two elements
        for (Advisor advisor : testBuildAdvisors()) {
            System.out.println(advisor);
        }
    }
}
