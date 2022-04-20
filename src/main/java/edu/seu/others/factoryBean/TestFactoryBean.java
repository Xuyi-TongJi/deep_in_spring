package edu.seu.others.factoryBean;

import edu.seu.others.factoryBean.env.Bean1;
import edu.seu.others.factoryBean.env.Bean1Factory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * FactoryBean本身是受spring容器管理，但FactoryBean生产的产品不是完全被Spring容器管理的
 *
 * FactoryBean的现状 --> 目前Spring的配置类就是一个工厂Bean，@Bean注解已经可以实现相同功能
 * [其作用是制造创建过程较为复杂的产品,如SqlSessionFactory ，而@Bean已经具有该功能]
 * 但该接口被大量使用，全面废弃较难
 */
@ComponentScan
public class TestFactoryBean {
    public static void main(String[] args) {
        testBeanFactory();
    }

    private static void testBeanFactory() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestFactoryBean.class);
        // 按名字getBean，获得产品，而非工厂Bean本身
        Bean1 bean1 = (Bean1)context.getBean("bean1");
        // 加&，可以取得BeanFactory
        Bean1Factory factory = (Bean1Factory)context.getBean("&bean1");
        System.out.println(bean1);
        context.close();
    }
}
