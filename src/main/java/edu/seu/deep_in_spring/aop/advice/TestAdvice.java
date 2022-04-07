package edu.seu.deep_in_spring.aop.advice;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 前置通知，环绕通知，返回通知，异常通知和后置通知在执行时统一被转换为MethodInterceptor环绕通知
 * [适配器模式和责任链模式]
 * 无论ProxyFactory用哪种方式创建代理，最后调用advice进行增强，即MethodInvocation
 * MethodInvocation要知道advice有哪些，所有其他通知都适合被转换为环绕通知[一层套一层]
 */
public class TestAdvice {


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

        @After("execution(* foo())")
        public void after() {
            System.out.println("after");
        }

        @AfterReturning("execution(* foo())")
        public void afterReturning() {
            System.out.println("afterReturning");
        }

        @AfterThrowing("execution(* foo())")
        public void afterThrowing() {
            System.out.println("afterThrowing");
        }

        @Around("execution(* foo())")
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
     * 解析不同的通知类型并将其转换为环绕通知
     */
    private static void testAdvisorTransfer() throws Throwable {
        // 最终创建的切面集合
        List<Advisor> list = new ArrayList<>();

        // 创建单例切面工厂，代表切面类Aspect是单例的
        SingletonAspectInstanceFactory factory = new SingletonAspectInstanceFactory(new Aspect());
        for (Method method : Aspect.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                String execution = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(execution);
                AspectJMethodBeforeAdvice advice = new AspectJMethodBeforeAdvice(method, pointcut, factory);

                // 切点 + 通知 -> advisor
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
            else if (method.isAnnotationPresent(AfterReturning.class)) {
                String execution = method.getAnnotation(AfterReturning.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(execution);
                AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(method, pointcut, factory);

                // 切点 + 通知 -> advisor
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
            else if (method.isAnnotationPresent(Around.class)) {
                String execution = method.getAnnotation(Around.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(execution);
                AspectJAroundAdvice advice = new AspectJAroundAdvice(method, pointcut, factory);

                // 切点 + 通知 -> advisor
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
        }
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(new Target());
        proxyFactory.addAdvisors(list);

        System.out.println("---------------------------");
        // 转换
        List<Object> lists = proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(Target.class.getMethod("foo"), Target.class);

        /*
            org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor@8e24743
            org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor@74a10858
            org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor@23fe1d71
            org.springframework.aop.aspectj.AspectJAroundAdvice: advice method [public java.lang.Object edu.seu.deep_in_spring.aop.advice.TestAdvice$Aspect.around(org.aspectj.lang.ProceedingJoinPoint) throws java.lang.Throwable]; aspect name ''
            结论：除了@Around以外的所有通知类型都会被转换为Advice环绕通知[适配器]
         */
        for (Object o : lists) {
            System.out.println(o);
        }

        Target target = new Target();
        // Spring在执行调用链前，会在代理工厂中加入ExposeInvocationInterceptor.INSTANCE[ADVICE] ，该通知可以将MethodInvocation放入当前线程，以便其他通知使用
        proxyFactory.addAdvice(ExposeInvocationInterceptor.INSTANCE);
        /*
            模拟执行调用链 -> ReflectiveMethodInvocation构造方法被保护，因此无法实例化
            MethodInvocation methodInvocation = new ReflectiveMethodInvocation(
                null, target, Target.class.getMethod("foo"), new Object[0], Target.class, lists
            );
            methodInvocation.proceed();
        */
    }

    public static void main(String[] args) {
        try {
            testAdvisorTransfer();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
