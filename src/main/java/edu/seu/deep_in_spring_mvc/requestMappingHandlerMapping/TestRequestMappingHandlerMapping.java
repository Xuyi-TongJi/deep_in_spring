package edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 * RequestMappingHandlerMapping实现了HandlerMapping接口
 * HandlerMapping接口可以建立请求路径和控制器方法之间的映射关系 --> 请求的路径映射
 * RequsetMappingHandlerMapping的实现逻辑：根据@RequestMapping注解实现路径映射
 *
 * 根据请求信息获取控制器方法信息[最终会得到一个执行链，即拦截器interceptors+控制器方法handlerMethod]
 */
public class TestRequestMappingHandlerMapping {
    /**
     * RequestMappingHandlerMapping工作流程；
     * 到容器中找到所有@Controller类，筛选这些控制器中加了@RequestMapping及其子注解的方法
     * 记录它们的路径以及对应的控制器方法
     */
    public static void main(String[] args) throws Exception {
        AnnotationConfigServletWebServerApplicationContext context
                = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);

        // 作用 解析@ReqeustMapping注解以及其派生注解，生成路径path和控制器方法的映射关系[在这个Bean初始化时就会生成]
        // ****在DispatcherServlet中，在其被加入到List容器中时生成[即DispatcherServlet初始化时生成]
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);

        // 获取映射结果Map
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        handlerMethods.forEach((k, v) -> {
            System.out.println(k + "=" + v);
        });
        /*
            {POST [/test2]}=edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping.MyController#test2(String)
            {GET [/test1]}=edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping.MyController#test1()
            {PUT [/test3]}=edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping.MyController#test3(String)
            { [/test4]}=edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping.MyController#test4()
         */

        // 根据请求获取控制器方法[封装为HandlerExecutionChain --> 处理器执行链 = 请求的所有拦截器interceptors + 控制器方法HandleMethod]
        HandlerExecutionChain chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/test1"));
        System.out.println(chain);
        /*
            HandlerExecutionChain with [edu.seu.deep_in_spring_mvc.requestMappingHandlerMapping.MyController#test1()] and 0 interceptors
         */
        context.close();
    }
}
