package edu.seu.others.autowired;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Autowired注入底层原理
 */
public class TestAutowired {
    public static void main(String[] args) {

    }

    private static void testAutowired() throws NoSuchFieldException, NoSuchMethodException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestAutowired.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        // 1. 根据成员变量类型注入
        DependencyDescriptor dd1 = new DependencyDescriptor(Bean1.class.getDeclaredField("bean2"), false);
        beanFactory.doResolveDependency(dd1, "bean1", null, null);
        // 2. 根据参数的类型注入
        Method setBean2 = Bean1.class.getDeclaredMethod("setBean2", Bean2.class);
        DependencyDescriptor dd2 = new DependencyDescriptor(new MethodParameter(setBean2, 0), false);
        beanFactory.doResolveDependency(dd2, "bean1", null, null);

        // 3. 结果包装为Optional<Bean>
        DependencyDescriptor dd3 = new DependencyDescriptor(Bean1.class.getDeclaredField("bean3"), false);
        // 增加一层内嵌  Optional --> Bean
        if (dd3.getDependencyType() == Optional.class) {
            dd3.increaseNestingLevel();
            Object bean2 = beanFactory.doResolveDependency(dd3, "bean1", null, null);
            // 将bean2封装为Optional类型
            Optional.ofNullable(bean2);
        }

        // 4. 结果封装为对象工厂类型[ObjectFactory, ObjectProvider]
        DependencyDescriptor dd4 = new DependencyDescriptor(Bean1.class.getDeclaredField("bean4"), false);
        if (dd4.getDependencyType() == ObjectFactory.class) {
            dd4.increaseNestingLevel();
            // 不调用ObjectFactory.getObject方法，不会生产给类产品[推迟对象的获取]
            ObjectFactory<Bean2> factory = () -> (Bean2)beanFactory.doResolveDependency(dd4, "bean1",
                    null, null);
        }
        // 5. 对@Lazy的处理
        // 作用：创建代理对象，在访问代理对象的代理方法时，才会创建真实的对象
        // 注入代理
        DependencyDescriptor dd5 = new DependencyDescriptor(Bean1.class.getDeclaredField("bean2"), false);
        ContextAnnotationAutowireCandidateResolver resolver = new ContextAnnotationAutowireCandidateResolver();
        resolver.setBeanFactory(beanFactory);
        // 如果有@Lazy，则会创建代理，没有则返回真实对象
        resolver.getLazyResolutionProxyIfNecessary(dd5, "bean1");
    }

    @Component
    static class Bean1 {
        @Autowired @Lazy
        private Bean2 bean2;

        @Autowired
        public void setBean2(Bean2 bean2) {
            this.bean2 = bean2;
        }

        @Autowired
        private Optional<Bean2> bean3;

        @Autowired
        private ObjectFactory<Bean2> bean4;
    }

    @Component("bean2")
    static class Bean2 {}
}
