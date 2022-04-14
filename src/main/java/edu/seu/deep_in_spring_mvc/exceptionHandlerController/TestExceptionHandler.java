package edu.seu.deep_in_spring_mvc.exceptionHandlerController;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Spring MVC异常处理流程 --> 处理控制器抛出的异常
 * disPatcherServlet ->
 * try{...}
 * catch (Exception ex) {
 *     disPatchException = ex;
 * }
 * catch (Throwable err) {
 *     disPatchException = new NestedServletException("Handler dispatch failed", err);
 * }
 * // 该方法会判断dispatchException是否为空，如果为空，则走视图渲染流程[mvc]，如果不为空则进行异常处理
 * processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
 * }
 *
 * 异常处理核心方法[由ExceptionResolver处理]
 * mv = processHandlerException(request, response, handler, exception);
 */
public class TestExceptionHandler {
    public static void main(String[] args) throws Exception {
        testNestedException();
    }

    /**
     * 测试两种返回类型[response/mav]的异常处理
     * **** ExceptionHandlerExceptionResolver只能处理Controller中抛出的异常
     */
    private static void testHandleException() throws NoSuchMethodException {
        ExceptionHandlerExceptionResolver handlerExceptionResolver
                = new ExceptionHandlerExceptionResolver();
        handlerExceptionResolver.setMessageConverters(
                List.of(new MappingJackson2HttpMessageConverter()));
        // 添加默认的参数解析器和返回值处理器 -> 解析@ResponseBody等注解
        handlerExceptionResolver.afterPropertiesSet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // 创建HandlerMethod -> Controller + Method
        HandlerMethod handlerMethod
                = new HandlerMethod(new Controller1(), Controller1.class.getMethod("foo"));
        ArithmeticException exception = new ArithmeticException("divided by zero");

        // 处理异常 返回值类型为@ResponseBody + Object
        /*
            检查handlerMethod所在的Controller中是否有标注为@ExceptionHandler的方法。
            如果有，则检测其方法参数是否支持exception[类型是否匹配]，如果匹配则反射调用该方法
         */
        handlerExceptionResolver.resolveException(request, response, handlerMethod, exception);

        // 获取响应内容[@ResponseBody注解由MessageConverter转换为json]

        // {"error":"divided by zero"}
        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));


        /* 异常处理器的返回值为ModelAndView */
        HandlerMethod method2 = new HandlerMethod(new Controller2(), Controller2.class.getMethod("bar"));
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        ModelAndView mav = handlerExceptionResolver.resolveException(request, response2, method2, exception);
        assert mav != null;
        //{error=divided by zero}
        System.out.println(mav.getModel());
        //test2
        System.out.println(mav.getViewName());
    }

    /**
     * 测试嵌套异常处理
     */
    private static void testNestedException() throws Exception {
        Exception e = new Exception("e1", new RuntimeException("e2", new IOException("e3")));
        ExceptionHandlerExceptionResolver handlerExceptionResolver
                = new ExceptionHandlerExceptionResolver();
        handlerExceptionResolver.setMessageConverters(
                List.of(new MappingJackson2HttpMessageConverter()));
        // 添加默认的参数解析器和返回值处理器 -> 解析@ResponseBody等注解
        handlerExceptionResolver.afterPropertiesSet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod method = new HandlerMethod(new Controller3(), Controller3.class.getMethod("foo"));
        handlerExceptionResolver.resolveException(request, response, method, e);
        // {"error":"e3"}
        // 嵌套循环会转换为一个Exception数组，并一个一个与@ExceptionHandler中的方法参数进行类型匹配
        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    /**
     * 测试异常处理方法的参数解析
     */
    private static void testExceptionHandlerParamResolver() throws NoSuchMethodException {
        Exception e = new Exception("e1");
        ExceptionHandlerExceptionResolver handlerExceptionResolver
                = new ExceptionHandlerExceptionResolver();
        handlerExceptionResolver.setMessageConverters(
                List.of(new MappingJackson2HttpMessageConverter()));
        // [HandlerExceptionResolver的初始化方法]添加默认的参数解析器和返回值处理器 -> 解析@ResponseBody等注解

        // 在该初始化方法中，添加了参数解析器和返回值解析器，其中包括ServletRequestMethodArgumentResolver
        handlerExceptionResolver.afterPropertiesSet();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerMethod method = new HandlerMethod(new Controller4(), Controller4.class.getMethod("foo"));
        handlerExceptionResolver.resolveException(request, response, method, e);
        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    static class Controller1 {
        public void foo() {}

        @ExceptionHandler
        @ResponseBody
        public Map<String, Object> handle(ArithmeticException e) {
            return Map.of("error", e.getMessage());
        }
    }

    static class Controller2 {
        public void bar() {}

        @ExceptionHandler
        public ModelAndView handle(ArithmeticException e) {
            return new ModelAndView("test2", Map.of("error", e.getMessage()));
        }
    }

    static class Controller3 {
        public void foo() {}

        @ResponseBody
        @ExceptionHandler
        public Map<String, Object> handle(IOException e) {
            return Map.of("error", e.getMessage());
        }
    }

    static class Controller4 {
        public void foo() {}

        @ExceptionHandler
        @ResponseBody
        public Map<String, Object> handler(Exception e, HttpServletRequest request) {
            return Map.of("error", e.getMessage());
        }
    }
}