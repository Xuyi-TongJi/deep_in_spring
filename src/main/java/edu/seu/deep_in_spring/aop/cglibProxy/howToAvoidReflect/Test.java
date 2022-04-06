package edu.seu.deep_in_spring.aop.cglibProxy.howToAvoidReflect;

/**
 * 测试CGLIB避免反射调用实现方法增强的原理以及其与jdk反射优化的区别
 *
 * 核心原理：代理类内部生成了两个FastClass子类[本质上也是两个代理类]，
 * 其中一个配合Target类和methodProxy.invoke使用；另一个配合Proxy类和methodProxy.invokeSuper使用
 */
public class Test {
    public static void main(String[] args) {

    }
}
