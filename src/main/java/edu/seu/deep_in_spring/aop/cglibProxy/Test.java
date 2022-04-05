package edu.seu.deep_in_spring.aop.cglibProxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class Test {
    /**
     * 测试cglib动态代理，该代理可以代理没有继承接口的类
     */
    public static void main(String[] args) {
        // 创建代理对象和目标对象
        Proxy proxy = new Proxy();
        Target target = new Target();

        // 执行目标对象的setMethodInterceptor 自定义一个匿名内部类，该匿名内部类可以定义方法增强的逻辑
        proxy.setMethodInterceptor(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("before...");
                // 目标对象执行真正的method方法

                // 1. 反射调用
                // Object res = method.invoke(target, args);

                // 2. 内部无反射的调用名，结合目标对象
                // Object res = methodProxy.invoke(target, args);

                // 3. 内部无反射的调用，结合代理对象
                Object res = methodProxy.invokeSuper(proxy, args);
                System.out.println("after...");
                return res;
            }
        });
        // proxy对象调用相应方法是想方法增强
        proxy.save();
        proxy.save(1);
        proxy.save(1L);

        /*
            与jdk调用的区别：
            cglib通过methodProxy可以实现每次调用都直接调用而非反射调用
         */
    }
}
