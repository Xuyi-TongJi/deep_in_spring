package edu.seu.deep_in_spring_mvc.requestMappingHandlerMappingAdapter;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * RequestMappingHandlerAdapter实现了HandlerAdapter接口
 * HandlerAdapter接口可以调用控制器方法[适配器模式]
 *
 * 该类实现逻辑是调用带@RequestMapping及其派生注解标注的控制器方法
 *
 * [***@RequestBody等参数解析等工作不是由Mapping和Adapter做的，而是由ReqeustMappingHandlerAdpater中的ArgumentResolver做的]
 */
public class TestRequestMappingHandlerAdapter {
    public static void main(String[] args) throws Exception {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        RequestMappingHandlerAdapter adapter = context.getBean(RequestMappingHandlerAdapter.class);

        // HandlerMapping将处理器方法封装为相应的HandlerMethod, 而HandlerAdapter可以调用HandlerMapping --> invokeHandlerMethod方法
        Method method = RequestMappingHandlerAdapter.class
                .getDeclaredMethod("invokeHandlerMethod", HttpServletRequest.class, HttpServletResponse.class, HandlerMethod.class);
        method.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test2");
        request.setParameter("name", "张三");

        // 获取执行链
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);
        HandlerExecutionChain chain = mapping.getHandler(request);

        // 使用adapter真正调用控制器方法
        System.out.println("----------------------------------");
        assert chain != null;
        // 23:58:03.748 [main] DEBUG edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping.MyController - test1()
        // 00:03:53.864 [main] DEBUG edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping.MyController - test2(张三)
        // method.invoke(adapter, request, new MockHttpServletResponse(), chain.getHandler());

        // 获取参数解析器
        List<HandlerMethodArgumentResolver> argumentResolvers = adapter.getArgumentResolvers();
/*        System.out.println("所有参数解析器");
        for (HandlerMethodArgumentResolver argumentResolver : argumentResolvers) {
            System.out.println(argumentResolver);
        }*/
        List<HandlerMethodReturnValueHandler> returnValueHandlers = adapter.getReturnValueHandlers();
/*        System.out.println("所有返回值解析器");
        for (HandlerMethodReturnValueHandler handlerMethodReturnValueHandler : returnValueHandlers) {
            System.out.println(handlerMethodReturnValueHandler);
        }*/


        /* 测试自定义参数解析器 */

        // make request
        MockHttpServletRequest requestToken = new MockHttpServletRequest("PUT", "/test3");
        requestToken.addHeader("token", "fsfadfsadf");

        // getHandler of request by handlerMapping
        HandlerExecutionChain chain1 = mapping.getHandler(requestToken);

        // do invokeHandlerMethod by handlerAdapter
        method.invoke(adapter, requestToken, new MockHttpServletResponse(), chain1.getHandler());
        // 00:44:42.687 [main] DEBUG edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping.MyController - test3(fsfadfsadf)


        /* 测试自定义返回值解析器 */
        System.out.println("----------------------");
        MockHttpServletRequest requestYml = new MockHttpServletRequest("GET", "/test5");
        HandlerExecutionChain chain2 = mapping.getHandler(requestYml);

        // Mapping: 19:26:21.669 [main] DEBUG org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped to edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping.MyController#getUser(Integer)
        MockHttpServletResponse responseYml = new MockHttpServletResponse();
        method.invoke(adapter, requestYml, responseYml, chain2.getHandler());

        // 获取响应内容
        byte[] contentAsString = responseYml.getContentAsByteArray();
        // !!edu.seu.deep_in_spring_mvc.requestMappingHandlerMappingAdapter.pojo.User {id: 1,
        //       name: xuyi}
        System.out.println(new String(contentAsString, StandardCharsets.UTF_8));
        context.close();
    }
}
