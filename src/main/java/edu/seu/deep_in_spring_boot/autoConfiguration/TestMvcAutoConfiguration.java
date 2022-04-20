package edu.seu.deep_in_spring_boot.autoConfiguration;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 与web MVC相关的自动配置类
 */
public class TestMvcAutoConfiguration {
    public static void main(String[] args) {
        testMvcAutoConfiguration();
    }

    private static void testMvcAutoConfiguration() {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext();
        context.registerBean(Config.class);
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            String source = context.getBeanDefinition(name).getResourceDescription();
            if (source != null) {
                System.out.println(name + "=====" + source);
            }
        }
        context.close();
    }

    @Configuration
    @Import(MySelector.class)
    static class Config{}

    static class MySelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    // 内嵌tomcat服务器工厂
                    ServletWebServerFactoryAutoConfiguration.class.getName(),
                    // dispatcherServlet
                    DispatcherServletAutoConfiguration.class.getName(),
                    // dispatcherServlet运行时需要的各种组件[适配器，映射器]
                    WebMvcAutoConfiguration.class.getName(),
                    // Error处理 basic error controller
                    ErrorMvcAutoConfiguration.class.getName()
            };
        }
    }
}
