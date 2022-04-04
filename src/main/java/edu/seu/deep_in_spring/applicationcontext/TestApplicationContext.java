package edu.seu.deep_in_spring.applicationcontext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.Controller;

import java.util.Arrays;

/**
 * ApplicationContext实现类
 */
public class TestApplicationContext {
    public static void main(String[] args) {
        //testClassPathXmlApplicationContext();
        //testAnnotationConfigApplicationContext();
        testAnnotationConfigServletWebServer();
    }

    /**
     * ClassPathXmlApplicationContext 基于类路径下读取xml文件读取配置文件来创建ApplicationContext容器
     */
    private static void testClassPathXmlApplicationContext() {
        ClassPathXmlApplicationContext context
                = new ClassPathXmlApplicationContext("test-spring-config.xml");
        Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);
        /*
            原理：
            组合模式
            ClassPathXmlApplicationContext内部组合了一个beanFactory负责管理bean，而xmlBeanReader负责读取xml文件
            原理: 先创建一个beanFactory[负责管理bean] 再创建一个xmlBeanReader 用于读取xml文件
         */
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);

        reader.loadBeanDefinitions(new ClassPathResource("test-spring-config.xml"));
    }

    /**
     * FileSystemApplicationContext 基于文件系统读取xml配置文件来创建ApplicationContext容器
     * 可基于绝对和相对路径
     */
    private static void testFileSystemXmlApplicationContext(){
        FileSystemXmlApplicationContext context
                = new FileSystemXmlApplicationContext("...");
    }

    /**
     * 基于java配置类来创建ApplicationContext
     */
    private static void testAnnotationConfigApplicationContext() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Config.class);
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }

    /**
     * 基于java配置类来创建ApplicationContext，该context适用于web环境
     */
    private static void testAnnotationConfigServletWebServer() {
        AnnotationConfigServletWebServerApplicationContext context
                = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);
    }

    @Configuration
    static class Config {
        @Bean("bean1")
        public Bean1 getBean1() {
            return new Bean1();
        }

        @Bean("bean2")
        public Bean2 getBean2(@Qualifier("bean1") Bean1 bean1) {
            Bean2 bean2 = new Bean2();
            bean2.setBean1(bean1);
            return bean2;
        }
    }

    /**
     * 基于web的配置类
     * 必须要有：ServletWebServerFactory, DispatcherServlet 并把后者注册到前者(DispatcherServletRegistrationBean)
     * [内嵌tomcat服务器的工作原理]
     */
    @Configuration
    static class WebConfig {

        /**
         * TomcatServlet工厂类
         */
        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        /**
         * DispatcherServlet
         */
        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        /**
         * 注册dispatcherServlet到tomcat服务器
         */
        @Bean
        public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(
                DispatcherServlet dispatcherServlet) {
            // path: 让所有请求都先经过DispatcherServlet
           return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        }

        /**
         * testController
         */
        @Bean("/")
        public Controller controller() {
            return (request, response) -> {
                response.getWriter().write("hello");
                return null;
            };
        }
    }
}
