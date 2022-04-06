package edu.seu.deep_in_spring.aop.cglibProxy.howToAvoidReflect;

import edu.seu.deep_in_spring.aop.cglibProxy.Proxy;
import org.springframework.cglib.core.Signature;

/**
 * 模拟ProxyFastClass实现代理功能
 * 该代理类配合Proxy类使用，能够实现无反射的原始方法调用
 * 注意只能实现原始方法，不能实现增强方法，因为增强方法是在MethodInterceptor实现的
 */
public class ProxyFastClass {
    /**
     * saveSuper是Proxy类中的方法，其实现逻辑为super.save()，即原始方法
     * 需要使用这个类调用这个方法，即可实现代理时无反射的原始方法调用
     * 原理与TargetFastClass相同
     */
    static Signature s0 = new Signature("saveSuper", "()V");
    static Signature s1 = new Signature("saveSuper", "(I)V");
    static Signature s2 = new Signature("saveSuper", "(J)V");

    public int getIndex(Signature signature) {
        if (s0.equals(signature)) {
            return 0;
        } else if (s1.equals(signature)) {
            return 1;
        } else if (s2.equals(signature)) {
            return 3;
        }
        return -1;
    }

    /**
     * 通过代理类执行目标类的原始方法
     */
    public Object invokeSuper(int index, Object obj, Object[] args) throws NoSuchMethodException {
        if (index == 0) {
            ((Proxy) obj).saveSuper();
            return null;
        } else if (index == 1) {
            ((Proxy) obj).saveSuper((int)args[0]);
            return null;
        } else if (index == 2) {
            ((Proxy) obj).saveSuper((long)args[0]);
            return null;
        }
        throw new NoSuchMethodException();
    }
}

class TestClass1 {
    public static void main(String[] args) throws NoSuchMethodException {
        ProxyFastClass methodProxy = new ProxyFastClass();
        Proxy proxy = new Proxy();
        methodProxy.invokeSuper(0, proxy, new Object[0]); // save
        methodProxy.invokeSuper(1, proxy, new Object[]{1}); // save1
        methodProxy.invokeSuper(2, proxy, new Object[]{1L}); // save1L
    }
}
