package edu.seu.deep_in_spring.aware;

import org.springframework.context.support.GenericApplicationContext;

/**
 * 实现Aware接口的各子接口可以实现对Bean, ApplicationContext等信息的获取
 */
public class TestAware {
    public static void main(String[] args) {
        testApplicationContextAware();
    }

    /**
     * 测试BeanNameAware接口，方法逻辑:在创建Bean成功后，打印Bean的名字
     */
    private static void testBeanNameAware() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("myBean", MyBean.class);
        context.refresh();
        context.close();
    }

    /**
     * 测试ApplicationContextAware和InitializingBean接口, 方法逻辑：在创建Bean成功后，打印容器名称
     */
    private static void testApplicationContextAware() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("myBean2", MyBean2.class);
        context.refresh();
        context.close();
    }

    /*
        @Autowired, @PreDestroy和@PostConstruct注解可以实现类似功能，和Aware接口和InitialingBean接口的区别？
        @Autowired等接口由BeanPostProcessor解析，属于扩展功能
        而Aware接口输入内置功能，不加任何扩展Spring就能够识别
        某些情况下，内置功能不会失效，但是扩展功能可能会失效
        比如：context中没有注入对应的BeanPostProcessor，@Autowired等接口就会失效
     */

    /*
        context.refresh()执行顺序
        1. 从beanFactory中找到所有的Bean工厂后处理器来执行[完成对所有BeanDefinition的创建]
        2. 添加Bean后处理器[在每个Bean创建时完成其扩展功能,例如@Autowired, @Resource自动注入 @PostConstruct和@PreDestroy等]
        3. 初始化单例[执行了2后，在初始化单例时就会执行这些扩展功能]
            3.1 依赖注入扩展 @Value @Autowired @Resource
            3.2 初始化扩展 @PostConstruct
            3.3 执行Aware和InitializingBean[非扩展功能]
            3.4 创建成功
     */
}
