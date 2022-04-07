package edu.seu.deep_in_spring.aop.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 职责链模式
 * [在代理方法执行时，静态代理[代理逻辑无参数]是不需要切点methodMatcher,只需要通知MethodInterceptor]
 * 在所有通知均转化为AdviceMethodInterceptor并形成链后，模拟实现AdviceMethodInterceptor的执行
 */
public class TestInvocationChain {

    public static void main(String[] args) throws NoSuchMethodException {
        Target target = new Target();
        List<MethodInterceptor> methodInterceptors = List.of(new Advice1(), new Advice2());
        // 每一个增强方法都会被创建一个MethodInvocation对象，而适配方法的逻辑不是在通知部分实现的，而是在解析切点pointcut时实现的
        MyInvocation invocation = new MyInvocation(target, Target.class.getMethod("foo"), new Object[0], methodInterceptors);
        try {
            /*
                实现对方法foo的嵌套调用
                Advice1 before
                Advice2 before
                Target foo()
                Advice2 after
                Advice1 after
             */
            System.out.println(invocation.proceed());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static class Target {
        public void foo() {
            System.out.println("Target foo()");
        }
    }

    static class Advice1 implements MethodInterceptor {

        /**
         * 责任链模式：调用下一个通知或目标[形成一个chain]
         *
         * @param invocation invocation
         */
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advice1 before");
            // 调用下一个通知或目标 -> 原理：递归
            Object result = invocation.proceed();
            System.out.println("Advice1 after");
            return result;
        }
    }

    static class Advice2 implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advice2 before");
            Object result = invocation.proceed();
            System.out.println("Advice2 after");
            return result;
        }
    }

    /**
     * MethodInterceptor执行链的执行器
     */
    static class MyInvocation implements MethodInvocation {

        /**
         * 目标对象
         */
        private Object target;
        /**
         * 目标方法
         */
        private Method method;
        /**
         * 目标方法参数
         */
        private Object[] args;

        /**
         * interceptors数组，包含所有被适配器转换过的MethodInterceptor[一个Interceptor对应一个Advisor]
         */
        private List<MethodInterceptor> interceptors;
        private int index = 1;

        public MyInvocation(Object target, Method method, Object[] args, List<MethodInterceptor> interceptors) {
            this.target = target;
            this.method = method;
            this.args = args;
            this.interceptors = interceptors;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object[] getArguments() {
            return args;
        }

        /**
         * 调用每一个AdviceMethodInterceptor的通知逻辑或目标
         * 递归 proceed -> interceptor.invoke -> invoke::proceed
         *
         * @return 调用返回的最终结果
         */
        @Override
        public Object proceed() throws Throwable {
            int cnt = interceptors.size();
            if (index <= cnt) {
                // 调用相应通知方法
                return interceptors.get(index++ - 1).invoke(this);
            }
            // 调用目标方法
            Object res = method.invoke(target, args);
            // 重置index
            index = 1;
            return res;
        }

        @Override
        public Object getThis() {
            return target;
        }

        @Override
        public AccessibleObject getStaticPart() {
            return method;
        }
    }
}
