package edu.seu.others.indexed;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * 注解@Indexed的原理
 * pom.xml依赖：spring-context-indexer
 *
 * Spring5.0利用@Indexed加速了组件扫描 -> 在编译时就进行扫描，并将结果存入spring.component文件，在运行时只需要读该文件即可，不需要走jar包的扫描流程
 *
 * 功能：在编译阶段找类中加入@Indexed的注解，如果有，则加入spring.component文件中
 * @Component注解中组合了@Indexed注解
 */
public class TestIndexed {
    public static void main(String[] args) {
        testIndexed();
    }

    private static void testIndexed() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 组件扫描核心类
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
        scanner.scan(TestIndexed.class.getPackageName());

        for (String name : beanFactory.getBeanDefinitionNames()) {
            // bean1 bean2 bean3 --> 在META-INF/spring.component中
            System.out.println(name);
        }
    }
}
