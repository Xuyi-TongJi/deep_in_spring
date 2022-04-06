package edu.seu.deep_in_spring.aop.cglibProxy;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class Proxy extends Target{

    private MethodInterceptor methodInterceptor;

    /**
     * java.reflect 方法
     */
    static Method save0;
    static Method save1;
    static Method save2;
    static MethodProxy save0Proxy;
    static MethodProxy save1Proxy;
    static MethodProxy save2Proxy;

    static {
        try {
            save0 = Target.class.getMethod("save");
            save1 = Target.class.getMethod("save", int.class);
            save2 = Target.class.getMethod("save", long.class);
            // 创建MethodProxy静态成员
            // arg1 & arg2 目标和代理对象 arg3 参数 ()代表无参 I代表int J代表long V表示返回值为void[字节码文件规则] save 和 saveSuper分别是Proxy.class中带增强功能的方法和原始方法名
            save0Proxy = MethodProxy.create(Target.class, Proxy.class, "()V", "save", "saveSuper");
            save1Proxy = MethodProxy.create(Target.class, Proxy.class, "(I)V", "save", "saveSuper");
            save2Proxy = MethodProxy.create(Target.class, Proxy.class, "(J)V", "save", "saveSuper");

        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError();
        }
    }

    /**
     * 三个save都是带增强功能的方法
     */
    @Override
    public void save() {
        try {
            /*
                methodInterceptor.intercept方法参数：执行方法的对象[this], 待增强的方法Method对象，方法参数, methodProxy逻辑
             */
            methodInterceptor.intercept(this, save0, new Object[0], save0Proxy);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void save(int i) {
        try {
            methodInterceptor.intercept(this, save1, new Object[]{i}, save1Proxy);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(long l) {
        try {
            methodInterceptor.intercept(this, save2, new Object[]{l}, save2Proxy);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    /**
     * 带有原始功能的save方法
     */
    public void saveSuper() {
        super.save();
    }

    public void saveSuper(int i) {
        super.save(i);
    }

    public void saveSuper(long l) {
        super.save(l);
    }
}
