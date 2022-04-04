package edu.seu.deep_in_spring;

import edu.seu.deep_in_spring.event.UserRegisteredEvent;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

@SpringBootApplication
public class DeepInSpringApplication {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException {
        ConfigurableApplicationContext context = SpringApplication.run(DeepInSpringApplication.class, args);

        // 通过反射将DefaultSingletonBeanRegistry中的bean取出 (singletonObjects -- concurrentHashMap)
        Field singletonObjects =
                DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        singletonObjects.setAccessible(true);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        // Field类get方法，返回指定对象(arg1)该字段的值
        Map<String, Object> map = (Map<String, Object>)singletonObjects.get(beanFactory);
       /* System.out.println(map);
        map.entrySet().stream().filter(e -> e.getKey().startsWith("component"))
                .forEach(e -> {
                    System.out.println(e.getKey() + "  " + e.getValue());
                });*/

        // spring ApplicationContext国际化
        // context.getMessage("xxx", null, Locale.CHINA);

        // spring Application 资源获取
        Resource[] resources = context.getResources("classpath:application.properties");
        Resource[] resources1 = context.getResources("classpath*:META-INF/spring.factories");
        /*for (Resource resource : resources) {
            System.out.println(resource);
        }*/
        // spring ApplicationContext 整合environment环境 获取配置信息(可以来自于系统环境变量，spring配置等)
        //context.getEnvironment().getProperty("server.port");
        //context.getEnvironment().getProperty("java_home");

        // spring ApplicationContext 事件发布与监听
        // spring中任何一个组件都可以作为监听器
        context.publishEvent(new UserRegisteredEvent(context));
    }
}
