package edu.seu.deep_in_spring_boot.autoConfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * Spring Boot 自动配置原理
 * 自动配置类本质上也是一个配置类
 * 自动配置类管理的Bean是有通用性质的Bean
 *
 * AutoConfiguration实际上就是根据@Import注解选择需要导入的自动配置类，根据自动配置类的逻辑[@ConditionOn..]和spring环境自动选择最终需要注入的Bean
 */
public class TestAutoConfiguration {
    public static void main(String[] args) {

    }

    private static void testImportConfiguration() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", Config.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.refresh();
        context.close();
    }

    /**
     * 假定这是本项目的一个配置类
     */
    @Configuration
    /**
     * 使用@Import注解和选择器倒入第三方配置类
     * 如果第三方Bean和本项目Bean重名，则本项目Bean优先级较高[springboot默认不允许覆盖]
     */
    @Import(MyImportSelector.class)
    static class Config {

    }

    /**
     * 实现ImportSelector接口配合@Import注解使用
     * DeferredImportSelector会先解析本项目中的Bean，再解析第三方的Bean。区别同名优先级
     */
    static class MyImportSelector implements DeferredImportSelector {

        /**
         * 方法返回值是一个字符串数组，表示@Import配合该选择器使用时，会导入的配置类
         * 实现：将这些需要导入的配置类写在spring.factories配置文件中
         */
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            List<String> configurations = SpringFactoriesLoader.loadFactoryNames(MyImportSelector.class, null);
            String[] arr = new String[configurations.size()];
            return configurations.toArray(arr);
        }
    }

    /**
     * 假定这是一个第三方提供的配置类
     */
    @Configuration
    static class AutoConfiguration1 {

        @Bean
        // 当spring容器中缺失Bean1时，才注入当前Bean1
        @ConditionalOnMissingBean
        public Bean1 bean1() {
            return new Bean1();
        }
    }

    static class Bean1 {
        public static final Long ID = 1L;
    }
}
