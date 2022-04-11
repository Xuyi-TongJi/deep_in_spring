package edu.seu.deep_in_spring_mvc.controllerAdvice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.text.ParseException;
import java.util.Locale;

/**
 * **@InitBinder注解可以加在@ControllerAdvice类中，也可以加在控制器类中
 * 前者为全局生效[对所有Controller生效]，后者只对当前控制器生效
 */
@Slf4j
public class InitBinder {
    /**
     * RequestMappingHandlerAdapter中有两个成员变量initBinderCache, initBinderAdviceCache
     * 前者用来存储当前Controller的initBinder[懒惰初始化]，而后者用来存储ControllerAdvice中的initBinder[饥饿初始化]
     */
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();

        // Aware
        handlerAdapter.setApplicationContext(context);
        handlerAdapter.afterPropertiesSet();

        log.debug("1. start");
        // 全局的InitBinder方法会在ApplicationContext容器创建时初始化

        // 每个控制器的InitBinder初始化时机: 当控制器中有方法被Adapter调用时会初始化，且存入ConcurrentHashMap，不会初始化第二次
        // 懒惰初始化
    }
}

@Configuration
class WebConfig {

    @ControllerAdvice
    static class MyControllerAdvice {
        @org.springframework.web.bind.annotation.InitBinder
        public void binder1(WebDataBinder binder) {
            binder.addCustomFormatter(new MyDataFormatter("controllerAdvice"));
        }
    }

    @Controller
    static class Controller1 {
        @org.springframework.web.bind.annotation.InitBinder
        public void binder2(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDataFormatter("controller1"));
        }

        public void foo() {
        }
    }

    @Controller
    static class Controller2 {
        @org.springframework.web.bind.annotation.InitBinder
        public void binder3(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDataFormatter("controller2"));
        }

        @org.springframework.web.bind.annotation.InitBinder
        public void binder4(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDataFormatter("controller2"));
        }

        public void bar() {
        }
    }
}

@Slf4j
class MyDataFormatter implements Formatter<String> {

    private final String desc;

    public MyDataFormatter(String str) {
        desc = str;
    }

    @Override
    public String parse(String text, Locale locale) throws ParseException {
        log.info("{},  parse...", desc);
        return null;
    }

    @Override
    public String print(String object, Locale locale) {
        log.info("{},  print...", desc);
        return null;
    }
}
