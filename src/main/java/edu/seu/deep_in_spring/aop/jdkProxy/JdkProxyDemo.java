package edu.seu.deep_in_spring.aop.jdkProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxyDemo {

    interface Foo {
        void foo();
    }

    static class Target implements Foo {
        public void foo() {
            System.out.println("target foo");
        }
    }

    /**
     * jdk只能针对接口代理
     */
    public static void main(String[] args) {
        ClassLoader loader = JdkProxyDemo.class.getClassLoader();

        // 创建目标对象
        Target target = new Target();

        /*
            创建代理对象
            arg 1 -> 类加载器
            代理类 --> 在运行期间直接生成代理类的字节码 类加载器用于加载生成的字节码
            arg 2 -> 代理类所实现的接口[Class数组]
            arg 3 -> InvocationHandler 代理类执行方法时的行为, 根据方法的不同可做不同行为。匿名内部类
        */
        Foo proxy = (Foo) Proxy.newProxyInstance(loader, new Class[]{Foo.class}, new InvocationHandler() {
            /**
             *
             * @param proxy 代理对象
             * @param method 方法对象Method[reflect]
             * @param args 方法参数
             * @return 方法返回值
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("before");
                if (method.getName().equals("foo")) {
                    // 调用目标对象的目标方法
                    System.out.println("foo now !");
                }
                Object result = method.invoke(target, args);
                System.out.println("after");
                return result;
            }
        });

        // 通过代理对象调用Foo接口的foo()方法
        proxy.foo();
        /*
            目标对象和代理对象是兄弟关系，都实现了相应接口
         */
    }
}
