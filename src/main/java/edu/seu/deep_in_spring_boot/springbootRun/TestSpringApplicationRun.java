package edu.seu.deep_in_spring_boot.springbootRun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * SpringApplication::run方法执行流程
 * 在该方法中将真正创建并初始化ApplicationContext容器
 */
public class TestSpringApplicationRun {
    public static void main(String[] args) {
        //SpringApplication.run(TestSpringApplicationRun.class, args);
        /*
            1. run方法执行流程
                1.1 得到SpringApplicationRunListeners 即事件发布器[启动过程中的重要节点执行完毕后发布事件]
                    发布Application starting事件
                1.2 封装启动args
                1.3 准备Environment添加命令行参数
                1.4 ConfigurationPropertySources处理
                1.5 通过EnvironmentPostProcessorApplicationListener进行env后处理
                    application.properties --> resolved by StandardConfigDataLocationResolver
                    spring.application.json
                1.6 绑定spring.main到SpringApplication对象
         */

        // 1. 获取事件发布器实现类名
        SpringApplication app = new SpringApplication();
        app.addListeners(event -> System.out.println(event.getClass()));

        List<String> names =
                SpringFactoriesLoader.loadFactoryNames(SpringApplicationRunListener.class, TestSpringApplicationRun.class.getClassLoader());
        for (String name : names) {
            // org.springframework.boot.context.event.EventPublishingRunListener
            // spring.factory中的默认事件发布器
            System.out.println(name);
        }
    }
}
