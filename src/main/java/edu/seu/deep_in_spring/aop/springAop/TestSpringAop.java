package edu.seu.deep_in_spring.aop.springAop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Spring选择代理技术的规则
 * Spring切点实现[匹配规则]
 * Spring通知实现[增强逻辑]
 * Spring切面实现[切点+通知]
 */
public class TestSpringAop {

    /*
        1.两个切面概念
            aspect = 通知1(advice) + 切点1(pointcut) ...
            一个aspect切面是可以包含多组通知+切点的

            advisor = 更细粒度的切面，包含一个通知和切点
            1个通知 + 1个切点
            aspect在生效前会被拆解为多个advisor

        不同：
     */

    /*
        代理模式选择
        1. 如果目标实现了接口，用jdk实现
        2. 如果目标没有实现接口，用CGLIB实现[final 类不能使用jdk]
        3. proxyTargetClass = true 总是使用CGLIB实现
     */
    @Aspect
    static class MyAspect {

        @Before("execution(* foo())")
        public void before() {
            System.out.println("before");
        }

        /**
         * 注解@After, @Before定义了pointcut
         * 而方法的逻辑定义了通知advice
         */
        @After("execution(* foo())")
        public void after() {
            System.out.println("after");
        }
    }
}
