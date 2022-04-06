package edu.seu.deep_in_spring.aop.springAop;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class TestPointcut {
    public static void main(String[] args) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // 设置切点pointcut
        pointcut.setExpression("execution(* foo())");
        // 设置通知advice[org.springframework.MethodInterceptor] 这个接口本质是一个@Around环绕通知
        MethodInterceptor advice = invocation -> {
            // 增强逻辑
            System.out.println("before");
            // 调用目标方法
            Object res = invocation.proceed();
            System.out.println("after");
            return res;
        };
        // 设置切面advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);

        // 创建代理工厂 -> 根据不同情况选择CGLIB或JDK代理
        ProxyFactory factory = new ProxyFactory();

        // 配置代理工厂 -> 设置目标类
        Target target = new Target();
        factory.setTarget(target);
        // 配置代理工厂 -> 设置切面
        factory.addAdvisor(advisor);

        // 如果不进行接口设置，则总是使用CGLIB
        factory.setInterfaces(target.getClass().getInterfaces());

        // 如果设置TargetClass=true 则总是使用CGLIB实现
        factory.setProxyTargetClass(true);

        // 创建代理对象
        I proxy = (I)factory.getProxy();

        // 此时采用cglib增强
        System.out.println(proxy.getClass());
        proxy.foo();
        // 不被增强
        proxy.bar();
    }

    interface I {
        void foo();
        void bar();
    }

    static class Target implements I {

        @Override
        public void foo() {
            System.out.println("target foo");
        }

        @Override
        public void bar() {
            System.out.println("target bar");
        }
    }
}