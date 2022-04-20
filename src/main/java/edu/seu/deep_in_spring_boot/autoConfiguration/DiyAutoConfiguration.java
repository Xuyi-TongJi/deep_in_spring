package edu.seu.deep_in_spring_boot.autoConfiguration;

import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 自定义自动配置
 */
public class DiyAutoConfiguration {
    public static void main(String[] args) {

    }
    /*
        Spring Boot 自动配置底层原理：@EnableAutoConfiguration
        @EnableAutoConfiguration注解实质上是@Import(AutoConfigurationImportSelector.class)
        public class AutoConfigurationSelector implements DeferredImportSelector {
                public String[] selectImports(AnnotationMetadata annotationMetadata) {
                    if (!this.isEnabled(annotationMetadata)) {
                        return NO_IMPORTS;
                    } else {
                        AutoConfigurationImportSelector.AutoConfigurationEntry autoConfigurationEntry
                            = this.getAutoConfigurationEntry(annotationMetadata);
                    return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
                    }
               }
        ****
        List<String> configurations = SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());

        // 最终，AutoConfigurationImportSelector选择器会从spring.factory中导入key为EnableAutoConfiguration.class的第三方配置类
        protected Class<?> getSpringFactoriesLoaderFactoryClass() {
            return EnableAutoConfiguration.class;
        }

        }
     */
    private static void testDiyAutoConfiguration() {

    }

    @Configuration
    //@Import(AutoConfigurationImportSelector.class)
    @EnableAutoConfiguration
    static class MyConfig {
    }

    /**
     * 第三方配置类
     * 在spring.factory中配置
     * org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
     *   edu.seu.deep_in_spring_boot.autoConfiguration.DiyAutoConfiguration.AutoConfiguration1
     * 并使用spring提供的AutoConfigurationImportSelector[@EnableAutoConfiguration注解]在项目配置类导入该自动配置类
     */
    @Configuration
    static class AutoConfiguration1 {

    }
}
