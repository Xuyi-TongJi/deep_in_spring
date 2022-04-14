package edu.seu.deep_in_spring_mvc.controllerAdvice;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理
 * 结合@ControllerAdvice 和 @ExceptionHandler注解
 */
public class TestExceptionHandler {
    public static void main(String[] args) throws NoSuchMethodException {
        testControllerAdviceExceptionHandler();
    }

    private static void testControllerAdviceExceptionHandler() throws NoSuchMethodException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        HandlerMethod method = new HandlerMethod(new Controller(), Controller.class.getMethod("foo"));

        Exception e = new Exception("e");

        // get ExceptionHandler from ApplicationContext
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        ExceptionHandlerExceptionResolver resolver
                = (ExceptionHandlerExceptionResolver)context.getBean(
                        "exceptionHandlerExceptionResolver");
        // {"error":"e"}
        resolver.resolveException(request, response, method, e);

        // response
        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    static class Controller {
        public void foo() {}
    }

    @Configuration
    static class WebConfig {
        @ControllerAdvice
        static class MyControllerAdvice {

            @ResponseBody
            @ExceptionHandler
            public Map<String, Object> handle(Exception e) {
                return Map.of("error", e.getMessage());
            }
        }

        /**
         * ExceptionHandlerExceptionResolver实现了InitializingBean接口实现了InitializingBean接口，在Bean初始化时会回调afterPropertiesSet()方法
         * 因为容器中有ControllerAdvice + ExceptionHandler 故在afterPropertiesSet时ExceptionHandlerExceptionResolver会缓存所有@ControllerAdvice中的@ExceptionHandler方法
         *
         * List<ControllerAdviceBean> adviceBeans = ControllerAdviceBean.findAnnotatedBeans(getApplicationContext());
         */
        @Bean
        public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
            ExceptionHandlerExceptionResolver handlerExceptionResolver = new ExceptionHandlerExceptionResolver();
            handlerExceptionResolver.setMessageConverters(
                    List.of(new MappingJackson2HttpMessageConverter()));
            return handlerExceptionResolver;
        }
    }
}
