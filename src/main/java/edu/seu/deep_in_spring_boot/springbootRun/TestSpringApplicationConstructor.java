package edu.seu.deep_in_spring_boot.springbootRun;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * SpringBoot启动过程
 * SpringApplication构造分析
 * SpringApplication.run()方法
 */
public class TestSpringApplicationConstructor {
    public static void main(String[] args) throws Exception {
        // ConfigurableApplicationContext run = SpringApplication.run(TestSpringApplicationRun.class);
        /*

            SpringApplication.run -> new SpringApplication(primarySources).run(args);
            1. SpringApplication类构造方法
                1.1 获取Bean Definition源 -> 来自配置文件，xml文件等 [引导类@SpringBootApplication是主源]
                1.2 推断应用类型[SERVLET web / REACTIVE web / 非web NONE -> 根据外部jar包推断 判断类路径下是否存在某个类]
                1.3 ApplicationContext初始化器[ApplicationContext创建后的扩展功能, 默认从配置文件中读取]
                1.4 监听器与事件
                1.5 主类推断[Spring应用程序中运行main方法的类]
                Spring容器在run方法中创建和初始化
            2. 成员方法run 方法的执行流程
         */
        SpringApplication spring = new SpringApplication(TestSpringApplicationConstructor.class);
        // 推断应用类型
        Method method = WebApplicationType.class.getDeclaredMethod("deduceFromClasspath");
        method.setAccessible(true);
        System.out.println(method.invoke(null)); // SERVLET

        // 手动实现并添加初始化器
        spring.addInitializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
            /**
             * 初始化器的扩展功能发生在applicationContext.refresh[初始化]前，可以进行applicationContext功能扩展
             */
            @Override
            public void initialize(ConfigurableApplicationContext applicationContext) {
                if (applicationContext instanceof GenericApplicationContext genericApplicationContext) {
                    genericApplicationContext.registerBean("bean2", Bean2.class);
                }
            }
        });

        // 手动添加并实现监听器 -> 对发布的事件进行监听处理
        spring.addListeners(new ApplicationListener<ApplicationEvent>() {
            /**
             * 捕捉事件
             * @param event 事件
             */
            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                System.out.println("\t事件为" + event.getClass());
            }
        });

        // 主类推断 推断main方法所在的类
        Method method1 = SpringApplication.class.getDeclaredMethod("deduceMainApplicationClass");
        method1.setAccessible(true);
        // class edu.seu.deep_in_spring_boot.springbootRun.TestSpringApplicationRun
        System.out.println(method1.invoke(spring));

        // ConfigurableApplicationContext context = spring.run(args);
    }

    @Component
    @Data
    static class Bean1 {
        public static final Integer ID = 1;
    }

    static class Bean2 {
        public static final Integer ID = 2;
    }

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
}
