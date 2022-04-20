package edu.seu.others.springProxy;

import edu.seu.others.springProxy.env.Bean1;
import org.springframework.aop.framework.Advised;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Spring代理设计特点
 *      1.依赖注入和初始化参与的是原始对象
 *      2.代理与目标是两个对象，二者的成员变量不共用数据
 *
 *  ****
 *      目标对象target在spring容器中是不存在的
 */
@SpringBootApplication
public class TestSpringProxy {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(TestSpringProxy.class, args);
        // 在spring容器创建过程中[初始化Bean1时]，对应的依赖注入和init方法不会被增强[原始对象]

        // 在context中get Bean1，得到是代理对象
        Bean1 proxy = context.getBean(Bean1.class);
        // 代理对象的方法会被增强
        proxy.init();

        // 获取目标对象
        if (proxy instanceof Advised advised) {
            Bean1 target = (Bean1)advised.getTargetSource().getTarget();

            //edu.seu.others.springProxy.env.Bean2@253494fc
            System.out.println(target.getBean2());
            // 代理对象不会走依赖注入/初始化流程，因此其成员变量bean2 = null
            // null
            System.out.println(proxy.bean2);
            // 但是代理对象在调用相应的get方法时，会的到目标对象已经被赋值的成员变量[代理对象的方法内部实质上是调用了目标对象的相应方法，因此可以得到值]
            // 目标对象并不在spring容器中
            //edu.seu.others.springProxy.env.Bean2@253494fc
            System.out.println(proxy.getBean2());

            /*
                static, final, private方法均不可增强[only 可以@Override的方法可以增强]
                [可以使用编译时增强或类加载时增强]
                反射调用可以被增强的方法，同样可以增强
             */
        }

        context.close();
    }

    private static void showProxyAndTarget() throws Exception {

    }

}
