package edu.seu.deep_in_spring.aop.springAop;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

/**
 * 测试切点匹配
 * 模拟实现Spring对@Transactional注解的解析
 */
public class TestPointcutMatch {
    public static void main(String[] args) throws NoSuchMethodException {
        // 根据切点表达式匹配
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* bar())");
        // 判断切点是否匹配
        System.out.println(pointcut.matches(T1.class.getMethod("foo"), T1.class)); // false
        System.out.println(pointcut.matches(T1.class.getMethod("bar"), T1.class)); // true

        // 根据注解匹配
        AspectJExpressionPointcut pointcut1 = new AspectJExpressionPointcut();
        pointcut1.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
        System.out.println(pointcut1.matches(T1.class.getMethod("foo"), T1.class)); // true
        System.out.println(pointcut1.matches(T1.class.getMethod("bar"), T1.class)); // false

        // 匹配@Transactional注解 -> 底层并不是使用注解切点表达式的匹配
        /*
            Spring中Transactional注解的实现有三种形式：可以注解方法，类和接口[类去实现这个接口]
            切点表达式无法解析注解在类上的@Transactional，因此Spring在实现时没有使用AspectJ切点表达式
        */
        /*  模拟实现Spring对@Transactional注解的解析  */
        StaticMethodMatcherPointcut pointcut2 = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // 方法上的注解信息[Spring封装类实现] 从继承树上找[实现的接口上的@Transactional注解也能解析]
                MergedAnnotations from
                        = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                if (from.isPresent(Transactional.class)) {
                    return true;
                }
                // 方法所在的类上的注解信息[反射实现]
                MergedAnnotations classFrom
                        = MergedAnnotations.from(targetClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                if (classFrom.isPresent(Transactional.class)) {
                    return true;
                }
                return false;
            }
        };
        System.out.println(pointcut2.matches(T1.class.getMethod("foo"), T1.class)); // true
        System.out.println(pointcut2.matches(T1.class.getMethod("bar"), T1.class)); // false
        System.out.println(pointcut2.matches(T2.class.getMethod("foo"), T2.class)); // true
        System.out.println(pointcut2.matches(T3.class.getMethod("foo"), T3.class)); // true
    }

    static class T1  {
        @Transactional
        public void foo() {}

        public void bar() {}
    }

    @Transactional
    static class T2 {
        public void foo() {}
    }

    @Transactional
    interface I {
    }

    static class T3 implements I {
        public void foo() {}
    }
}
