package edu.seu.deep_in_spring_boot.springbootRun;

import org.springframework.boot.*;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.boot.logging.DeferredLogs;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * SpringApplication::run方法执行流程
 * 在该方法中将真正创建并初始化ApplicationContext容器
 */
public class TestSpringApplicationRun {
    public static void main(String[] args) throws Exception {
        //SpringApplication.run(TestSpringApplicationRun.class, args);
        /*
            1. run方法执行流程
                1.1 得到SpringApplicationRunListeners 即事件发布器[启动过程中的重要节点执行完毕后发布事件]
                    发布Application starting事件
                <env>
                1.2 封装启动args
                1.3 准备Environment添加命令行[Command Line]参数
                1.4 ConfigurationPropertySources处理
                1.5 通过EnvironmentPostProcessorApplicationListener进行env后处理
                    application.properties --> resolved by StandardConfigDataLocationResolver
                    spring.application.json
                1.6 绑定env中spring.main前缀属性到SpringApplication对象
                <banner>
                1.7 打印banner[spring boot logo]
                <application context>
                1.8 创建容器 new ApplicationContext
                1.9 准备容器 -> 发布ApplicationContext已初始化事件
                1.10 加载BeanDefinition -> 发布Application Prepared事件
                1.11 refresh [初始化容器] -> 发布Application started事件
                <runner>
                1.12 执行runner[args<program arguments>相关事件] 发布Application ready 事件
         */
        testSpringApplicationRun(args);

    }

    /**
     * SpringBoot 启动测试[第一步，获取事件发布器实现类名]
     */
    private static void testSpringApplicationRunListeners(String[] args) throws Exception  {
        // 1. 获取事件发布器实现类名
        SpringApplication app = new SpringApplication();
        app.addListeners(event -> System.out.println(event.getClass()));

        List<String> names =
                SpringFactoriesLoader.loadFactoryNames(SpringApplicationRunListener.class, TestSpringApplicationRun.class.getClassLoader());
        for (String name : names) {
            // org.springframework.boot.context.event.EventPublishingRunListener
            // spring.factory中的默认事件发布器
            System.out.println(name);
            Class<?> clazz = Class.forName(name);
            Constructor<?> constructor = clazz.getConstructor(SpringApplication.class, String[].class);
            // 反射创建发布器对象
            SpringApplicationRunListener publisher = (SpringApplicationRunListener)constructor.newInstance(app, args);

            DefaultBootstrapContext defaultBootstrapContext = new DefaultBootstrapContext();
            GenericApplicationContext context = new GenericApplicationContext();

            /*
                各个阶段的事件都会被事件监听器监听到
             */

            // springboot开始启动时发布的事件
            publisher.starting(defaultBootstrapContext);
            // 环境信息准备完毕
            publisher.environmentPrepared(defaultBootstrapContext, new StandardEnvironment());
            // 在spring容器创建[new, 未refresh]并调用初始化器时调用该事件
            publisher.contextPrepared(context);
            // 所有Bean Definition创建完毕
            publisher.contextLoaded(context);
            // spring容器初始化完成[refresh]
            context.refresh();
            publisher.started(context);
            // springboot启动完毕发布的事件
            publisher.running(context);

            // springboot启动过程中出现了错误
            publisher.failed(context, new Exception("error"));
        }
    }

    /**
     *  SpringBoot启动过程测试[步骤2, 8~12 主要逻辑为spring容器的创建，功能扩展以及初始化, 最终执行与Program Arguments参数相关的逻辑]
     */
    @SuppressWarnings("all")
    private static void testSpringApplicationRun(String[] args) throws Exception {
        SpringApplication app = new SpringApplication();
        // 加载Bean Definition源 -> @Configuration源, xml，类路径 在run方法时会注册该sources中所有Bean Definition
        app.setSources(new HashSet<>());
        app.addListeners(event -> System.out.println("执行初始化器增强"));

        System.out.println("2.封装启动args，在ApplicationRunner中使用");
        DefaultApplicationArguments arguments = new DefaultApplicationArguments(args);

        System.out.println("8.根据app类型创建创建容器,类型在SpringApplication构造方法中推导");
        GenericApplicationContext context = createApplicationContext(WebApplicationType.SERVLET);

        System.out.println("9.准备容器[容器功能扩展],回调SpringApplication初始化器");
        Set<ApplicationContextInitializer<?>> initializers = app.getInitializers();
        for (ApplicationContextInitializer initializer : initializers) {
            initializer.initialize(context);
        }

        System.out.println("10.加载所有Bean Definition");
        // 获取注解定义的BeanDefinition读取器，可以注册[register]所有以注解定义的BeanDefinition
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(context.getDefaultListableBeanFactory());
        reader.register(Config.class);

        System.out.println("11.refresh容器, 根据Bean后处理器初始化所有的单例Bean");
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name + "    from:" +
                    context.getBeanFactory().getBeanDefinition(name).getResourceDescription());
        }

        System.out.println("12.执行runner[与main方法参数<Program Arguments>有关]");
        Map<String, CommandLineRunner> commandLineRunnerMap = context.getBeansOfType(CommandLineRunner.class);
        for (CommandLineRunner commandLineRunner : commandLineRunnerMap.values()) {
            commandLineRunner.run(args);
        }
        Map<String, ApplicationRunner> applicationRunnerMap = context.getBeansOfType(ApplicationRunner.class);
        for (ApplicationRunner applicationRunner : applicationRunnerMap.values()) {
            applicationRunner.run(arguments);
        }
    }

    /**
     * SpringBoot 启动测试[步骤2～7，与环境对象ApplicationEnvironment类[StandardEnvironment实现类]有关]
     * 对配置信息的抽象，配置信息的来源：系统环境变量,properties,yaml
     *
     * 配置来源的优先级：commandLine[args][最高] > application.properties[最低]
     */
    private static void testSpringApplicationRunEnv(String[] args) {
        System.out.println("3.创建ApplicationEnvironment对象，读取CommandLine来源的PropertySource");
        /*
            ApplicationEnvironment env = new ApplicationEnvironment();
            env.getPropertySource().addLast(new ResourcePropertySource(new ClassPathResource("application.properties")));
            for (PropertySource<?> ps : env.getPropertySources()) {
                System.out.println(ps);
            }
         */
        System.out.println("4.将ConfigurationPropertySources加入env,设置其优先级为最高，以解决命名冲突[firstName/first-name/first_name]");
        ConfigurationPropertySources.attach(new StandardEnvironment());

        System.out.println("5. Env后处理器 -> 获得env增强[功能扩展，补充更多的源]，比如，将application.properties中的信息加载入env");
        ConfigDataEnvironmentPostProcessor postProcessor
                = new ConfigDataEnvironmentPostProcessor(new DeferredLogs(), new DefaultBootstrapContext());
        // springboot对env后处理器的实现 -> 基于监听器和spring.factory

        SpringApplication app = new SpringApplication();
        app.addListeners(new EnvironmentPostProcessorApplicationListener());

        // 在第5步时，发布一个事件去从出发Environment后处理器事件监听器逻辑 -> 逻辑是调用spring.factory中所有env后处理器
        EventPublishingRunListener publisher = new EventPublishingRunListener(app, args);
        StandardEnvironment env = new StandardEnvironment();
        // 在环境准备完成后发布事件，使env后处理器生效
        publisher.environmentPrepared(new DefaultBootstrapContext(), env);

        System.out.println("6.绑定EnvironmentProperty中spring.main前缀的key value至SpringApplication");
        Binder.get(env).bind("spring.main", Bindable.ofInstance(app));

        System.out.println("7. 打印banner");
    }

    /**
     * 根据app类型创建ApplicationContext实现
     */
    @SuppressWarnings("all")
    private static GenericApplicationContext createApplicationContext(WebApplicationType webApplicationType) {
        GenericApplicationContext context = null;
        switch (webApplicationType) {
            case NONE -> context = new AnnotationConfigApplicationContext();
            case SERVLET -> context = new AnnotationConfigServletWebServerApplicationContext();
            case REACTIVE -> context = new ReactiveWebServerApplicationContext();
        }
        return context;
    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }

        @Bean
        public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public CommandLineRunner commandLineRunner() {
            return new CommandLineRunner() {
                /**
                 * @param args main方法参数
                 */
                @Override
                public void run(String... args) throws Exception {
                    System.out.println("commandLineRunner" + Arrays.toString(args));
                }
            };
        }

        @Bean
        public ApplicationRunner applicationRunner() {
            return new ApplicationRunner() {
                /**
                 * @param args 封装后的args对象[在run方法第二步封装] 可以区分两类不同的参数
                 */
                @Override
                public void run(ApplicationArguments args) throws Exception {
                    System.out.println("applicationRunner" + Arrays.toString(args.getSourceArgs()));
                    // 获得选项参数[-开头]
                    System.out.println(args.getOptionNames());
                    System.out.println(args.getOptionValues("server.port"));

                    // 非选项参数
                    System.out.println(args.getNonOptionArgs());
                }
            };
        }
    }

    static class Bean1 {
        public static final Long ID = 1L;
    }

    static class Bean2 {
        public static final Long ID = 2L;
    }
}