package edu.seu.deep_in_spring.aop.cglibProxy.howToAvoidReflect;


import edu.seu.deep_in_spring.aop.cglibProxy.Target;
import org.springframework.cglib.core.Signature;

/**
 * 模拟FastClass实现的过程，配合target使用和methodProxy.invoke使用
 * 该类创建的时机：调用 MethodProxy.create("相应方法")时，在生成字节码时直接生成该类的字节码
 */
public class TargetFastClass {

    /**
     * 这三个签名在调用MethodProxy的静态方法create时生成
     */
    static Signature s0 = new Signature("save", "()V");
    static Signature s1 = new Signature("save", "(I)V");
    static Signature s2 = new Signature("save", "(J)V");

    /**
     * 获取目标方法的编号
     * Target save() - 0 save(int) - 1 save(long) - 2 每个方法都有一个唯一的参数
     * @param signature 包括方法的名字，参数，返回值[在MethodProxy.create时，methodProxy就知道方法的签名]
     */
    public int getIndex(Signature signature) {
        // 判断签名参数对应哪个编号
        if (s0.equals(signature)) {
            return 0;
        } else if (s1.equals(signature)) {
            return 1;
        } else if (s2.equals(signature)){
            return 2;
        }
        return -1;
    }

    /**
     * 根据返回的方法编号，调用目标对象的方法[未增强的方法]
     * @param index 方法编号
     * @param target 目标类
     * @param args 参数
     * @return 目标方法返回值
     */
    public Object invoke(int index, Object target, Object[] args) throws NoSuchMethodException {
        if (index == 0) {
            ((Target)target).save();
            return null;
        } else if (index == 1) {
            ((Target)target).save((int)args[0]);
            return null;
        } else if (index == 2) {
            ((Target) target).save((long) args[0]);
            return null;
        }
        throw new NoSuchMethodException();
    }
}

class TestClass {
    public static void main(String[] args) throws NoSuchMethodException {
        TargetFastClass methodProxy = new TargetFastClass();
        int index = methodProxy.getIndex(new Signature("save", "()V"));
        Target target = new Target();
        // 模拟通过methodProxy调用target原始方法，从而避免反射调用
        methodProxy.invoke(index, target, new Object[0]);  // save
        methodProxy.invoke(1, target, new Object[]{1}); // save1
        methodProxy.invoke(2, target, new Object[]{1L}); // save1L
    }
}
