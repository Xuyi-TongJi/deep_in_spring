package edu.seu.deep_in_spring.beanPostProcessor.autoWired;

import edu.seu.deep_in_spring.beanPostProcessor.Bean1;
import edu.seu.deep_in_spring.beanPostProcessor.Bean2;
import edu.seu.deep_in_spring.beanPostProcessor.Bean3;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.env.StandardEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Autowired注解底层原理：通过反射给属性赋值。发生时间：类的实例化后，SpringBean的初始化前
 */
public class DigInAutowired {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("bean2", new Bean2());
        beanFactory.registerSingleton("bean3", new Bean3());
        // @Value
        beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // ${}解析器
        beanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders);


        AutowiredAnnotationBeanPostProcessor processor = new AutowiredAnnotationBeanPostProcessor();
        // 该后处理器会在beanFactory中find beans
        processor.setBeanFactory(beanFactory);

        Bean1 bean1 = new Bean1();
        System.out.println(bean1);
        // 执行依赖注入 @Autowired arg2 被注入的目标
        processor.postProcessProperties(null, bean1, "bean1");
        /*
            public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        // STEP1 - 找被注入的bean中哪些属性加了@Autowired注解
        InjectionMetadata metadata = this.findAutowiringMetadata(beanName, bean.getClass(), pvs);

        try {
            // STEP2 - 通过反射给属性赋值
            metadata.inject(bean, beanName, pvs);
            return pvs;
        } catch (BeanCreationException var6) {
            throw var6;
        } catch (Throwable var7) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", var7);
        }
    }
         */
        // PropertyValues:pvs
        Method method = AutowiredAnnotationBeanPostProcessor.class.
                getDeclaredMethod("findAutowiringMetadata", String.class, Class.class, PropertyValues.class);
        method.setAccessible(true);
        // 反射执行findAutowiringMetadata方法，获取bean1上加了@Autowired注解的成员信息
        InjectionMetadata metadata = (InjectionMetadata) method.invoke(processor, "bean1", Bean1.class, null);
        // System.out.println(bean1);


        // how to find Bean3

        Field bean3 = Bean1.class.getDeclaredField("bean3");
        DependencyDescriptor dd1 = new DependencyDescriptor(bean3, false);
        // find what to inject 根据成员变量Field bean3 得到类型信息 Bean3 然后从BeanFactory中找到类型为Bean3的bean3 最终反射到被注入的Bean1
        Object o = beanFactory.doResolveDependency(dd1, null, null, null);
        // Bean3
        System.out.println(o);
    }
}
