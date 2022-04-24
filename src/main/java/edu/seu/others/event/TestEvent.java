package edu.seu.others.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * spring 事件机制
 * 功能：实现主业务和分支业务的解藕
 * 观察者模式
 */
@Configuration
public class TestEvent {
    public static void main(String[] args) {
        testEvent();
    }

    private static void testEvent() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestEvent.class);
        MyService service = context.getBean(MyService.class);
        // 解析自定义注解实现监听器功能
        service.doBusiness();
    }

    /**
     * 实现SmartInitializingSingleton接口的Bean：在所有单例对象初始化完成时回调SmartInitializingSingleton中的方法afterSingletonsInstantiated()
     */
    @Bean
    public SmartInitializingSingleton smartInitializingSingleton(ConfigurableApplicationContext context) {
        return new SmartInitializingSingleton() {
            @Override
            public void afterSingletonsInstantiated() {
                // 执行@MyListener注解的解析，将所有的
                for (String name : context.getBeanDefinitionNames()) {
                    Object bean  = context.getBean(name);
                    for (Method method : bean.getClass().getMethods()) {
                        if (method.isAnnotationPresent(MyListener.class)) {
                            // 转换为ApplicationListener接口的实现类[适配器模式]
                            ApplicationListener<ApplicationEvent> listener = new ApplicationListener<ApplicationEvent>() {
                                @Override
                                public void onApplicationEvent(ApplicationEvent event) {
                                    Class<?> eventType = method.getParameterTypes()[0];
                                    // 事件类型与监听器方法参数类型一致，才能执行监听逻辑
                                    if (eventType.isAssignableFrom(event.getClass())) {
                                        try {
                                            // 执行@Listener标注的方法
                                            method.invoke(bean, event);
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            };
                            // 将事件监听器注册到applicationContext[的事件广播器]
                            // **** 观察者模式
                            context.addApplicationListener(listener);
                        }
                    }
                }
            }
        };
    }

    /**
     * 自定义事件类
     */
    static class MyEvent extends ApplicationEvent {
        /**
         * @param source 事件源，可以是string
         */
        public MyEvent(Object source) {
            super(source);
        }
    }

    /**
     * 自定义注解，实现与@EventListener相同的逻辑
     * EventListener解析底层原理
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface MyListener {
    }

    @Component
    @Slf4j
    static class MyService {
        /**
         * 事件发布器 --> 即ApplicationContext
         */
        @Autowired
        private ApplicationEventPublisher publisher;

        public void doBusiness() {
            log.debug("主线业务");
            // 发布事件
            publisher.publishEvent(new MyEvent("MyService.doBusiness"));
        }
    }

    /**
     * 自定义事件监听器,范型为该监听器监听哪个类型的事件
     */
    @Component
    @Slf4j
    static class SmsApplicationListener implements ApplicationListener<MyEvent> {

        @Override
        public void onApplicationEvent(MyEvent event) {
            log.debug("发送短信");
        }
    }

    @Component
    @Slf4j
    static class EmailApplicationListener implements ApplicationListener<MyEvent> {

        @Override
        public void onApplicationEvent(MyEvent event) {
            log.debug("发送邮件");
        }
    }

    /**
     * 基于注解@EventListener实现事件监听器
     */
    @Component
    @Slf4j
    static class SmsService {
        @EventListener
        public void listener(MyEvent event) {
            log.debug("发送短信");
        }
    }

    /**
     * 基于自定义注解@MyListener实现事件监听器
     */
    @Component
    @Slf4j
    static class MailService {
        @MyListener
        public void listener(MyEvent event) {
            log.debug("发送邮件");
        }
    }

    /**
     * 异步处理-> 基于spring中的线程池
     */
    @Bean
    public ThreadPoolTaskExecutor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        return executor;
    }

    /**
     * 自定义广播器以取代默认广播器，该广播器中注入线程池Bean以实现异步发送事件
     */
    @Bean
    public SimpleApplicationEventMulticaster applicationEventMulticaster(ThreadPoolTaskExecutor executor) {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(executor);
        return multicaster;
    }

    /**
     * 自定义实现事件发布器
     * 实现接口: ApplicationEventMulticaster
     * Spring内部默认实现：SimpleApplicationEventMulticaster
     */
    static abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster {
        @Override
        public void addApplicationListener(ApplicationListener<?> listener) {

        }

        @Override
        public void removeApplicationListener(ApplicationListener<?> listener) {

        }

        @Override
        public void removeApplicationListenerBean(String listenerBeanName) {

        }

        @Override
        public void removeApplicationListeners(Predicate<ApplicationListener<?>> predicate) {

        }

        @Override
        public void removeApplicationListenerBeans(Predicate<String> predicate) {

        }

        @Override
        public void removeAllListeners() {

        }

        @Override
        public void multicastEvent(ApplicationEvent event) {

        }
    }

    /**
     * 注入一个ApplicationEventMulticaster最终实现类
     */
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(
            ConfigurableApplicationContext context, ThreadPoolTaskExecutor executor) {
        return new AbstractApplicationEventMulticaster() {

            final List<GenericApplicationListener> listeners = new ArrayList<>();

            /**
             * 收集监听器[观察者模式]
             * spring容器初始化时回调该方法，将spring容器中所有实现了ApplicationListener接口的Bean名字收集，并添加到集合中
             */
            @Override
            public void addApplicationListenerBean(String listenerBeanName) {
                ApplicationListener listener = context.getBean(listenerBeanName, ApplicationListener.class);
                // 找到事件监听器的范型 --> 实现了ApplicationEvent的事件类型
                ResolvableType supportType = ResolvableType.forClass(listener.getClass()).getInterfaces()[0].getGeneric(0);
                // 将listener封装为支持事件类型检查的Listener(GenericApplicationListener)[装饰器模式]
                GenericApplicationListener genericApplicationListener = new GenericApplicationListener() {

                    /**
                     * 调用原始Listener的onApplicationEvent方法监听事件
                     */
                    @Override
                    public void onApplicationEvent(ApplicationEvent event) {
                        // 使用线程池发布任务
                        executor.submit(() ->
                           listener.onApplicationEvent(event)
                        );
                    }

                    /**
                     * 是否支持某事件类型
                     */
                    @Override
                    public boolean supportsEventType(ResolvableType eventType) {
                        return supportType.isAssignableFrom(eventType);
                    }
                };
                listeners.add(genericApplicationListener);
            }

            /**
             * 广播事件[发布事件]
             * @param event 事件类
             * @param eventType 事件类型
             */
            @Override
            public void multicastEvent(ApplicationEvent event, ResolvableType eventType) {
                for (GenericApplicationListener listener : listeners) {
                    if (listener.supportsEventType(ResolvableType.forClass(event.getClass()))) {
                        listener.onApplicationEvent(event);
                    }
                }
            }
        };
    }
}