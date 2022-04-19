package edu.seu.deep_in_spring_boot.autoConfiguration;

import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Aop AutoConfiguration
 */
public class TestAopAutoConfiguration {
    public static void main(String[] args) {
        testAopAutoConfiguration();
    }

    private static void testAopAutoConfiguration() {
        GenericApplicationContext context = new GenericApplicationContext();
        // 加入常用的Bean工厂和Bean后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
        context.registerBean(Config.class);
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            //org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$AspectJAutoProxyingConfiguration$CglibAutoProxyConfiguration
            //org.springframework.aop.config.internalAutoProxyCreator
            //org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$AspectJAutoProxyingConfiguration
            //org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
            System.out.println(name);
        }
    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

    }

    /**
     * Import选择器导入AopAutoConfiguration
     * AopAutoConfiguration实现：多层嵌套的静态内部类，根据@ConditionOn...注解以及配置文件选择最终要注入的Bean[底层有一个带有@Enable..注解的静态内部类]
     * 核心注解@EnableAspectJAutoProxy实际上也是@Import注解  // @Import({AspectJAutoProxyRegistrar.class})
     *
     * 默认情况下，最终将注入一个internalAutoProxyCreator[无论是否实现接口均采用CGLIB实现]Bean到spring容器中
     */
    static class MyImportSelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{AopAutoConfiguration.class.getName()};
        }
    }
}
