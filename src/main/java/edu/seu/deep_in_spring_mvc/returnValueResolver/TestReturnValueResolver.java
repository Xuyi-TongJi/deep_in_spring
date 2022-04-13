package edu.seu.deep_in_spring_mvc.returnValueResolver;

import edu.seu.deep_in_spring_mvc.requestMappingHandlerMappingAdapter.pojo.User;
import edu.seu.deep_in_spring_mvc.returnValueResolver.env.MyController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.*;
import org.springframework.web.util.UrlPathHelper;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestReturnValueResolver {
    public static void main(String[] args) throws Exception {
        //testModelAndViewReturnValueResolve();
        testResponseBody();
    }

    /**
     * 处理ModelAndView请求
     */
    private static void testModelAndViewReturnValueResolve() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Method test1 = MyController.class.getMethod("test1");
        MyController myController = new MyController();
        ModelAndView returnValue = (ModelAndView)test1.invoke(myController);
        // 获取返回值处理器
        HandlerMethodReturnValueHandlerComposite composite = getHandlerMethodReturnValueHandler();
        // 构造MethodHandler对象
        HandlerMethod handlerMethod = new HandlerMethod(myController, test1);
        // 获取handlerMethod返回值
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mav = new ModelAndViewContainer();
        if (composite.supportsReturnType(returnType)) {
            // 如果支持，则处理返回值
            /*
                返回值为ModelAndView，则处理逻辑是：
                1.将Model和View添加到ModelAndViewContainer中
                2.渲染视图[略]
             */
            composite.handleReturnValue(returnValue, returnType, mav,
                    new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse()));
            // view1
            // {name=zhangsan}
            System.out.println(mav.getViewName());
            System.out.println(mav.getModel());
        }
    }

    /**
     * 处理字符串类型返回值 -> 代表视图的名称
     */
    private static void testStringReturnValue() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Method test2 = MyController.class.getMethod("test2");
        MyController myController = new MyController();
        String returnValue = (String)test2.invoke(myController);
        HandlerMethodReturnValueHandlerComposite composite = getHandlerMethodReturnValueHandler();
        HandlerMethod handlerMethod = new HandlerMethod(myController, test2);
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mav = new ModelAndViewContainer();
        if (composite.supportsReturnType(returnType)) {
            /*
                返回值String，则处理逻辑是：
                将视图名称为返回值字符串的视图返回并渲染[渲染过程略]
             */
            composite.handleReturnValue(returnValue, returnType, mav,
                    new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse()));
        }
    }

    /**
     * 处理被@ModelAttribute[或省略该注解]注解标注的方法
     */
    private static void testModelAttribute() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Method test3 = MyController.class.getMethod("test3");
        MyController myController = new MyController();
        User returnValue = (User)test3.invoke(myController);
        HandlerMethodReturnValueHandlerComposite composite = getHandlerMethodReturnValueHandler();
        HandlerMethod handlerMethod = new HandlerMethod(myController, test3);
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mav = new ModelAndViewContainer();
        MockHttpServletRequest request = new MockHttpServletRequest();
        // 设置视图名称[如果有路径映射RequestMapping，则无需设置]
        request.setRequestURI("/test3");
        UrlPathHelper.defaultInstance.resolveAndCacheLookupPath(request);
        if (composite.supportsReturnType(returnType)) {
            /*
                解析@ModelAttribute，将方法返回值添加到ModelAndViewContainer中
             */
            composite.handleReturnValue(returnValue, returnType, mav,
                    new ServletWebRequest(request, new MockHttpServletResponse()));
            //{user=User(id=10, name=lisi)}
            System.out.println(mav.getModel());
        }
    }

    /**
     * 返回值为HttpEntity时的解析流程
     * HttpEntity代表整个响应对象
     * 后三种返回值处理与前三种返回值处理最大的不同在于，前三种通过视图解析来获取响应，而后三种直接返回响应本身
     * 底层：
     *   mavContainer.setRequestHandled(true) 一旦设置该标记，则不会走视图解析流程
     */
    private static void testHttpEntity() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Method test5 = MyController.class.getMethod("test5");
        MyController myController = new MyController();
        HttpEntity<User> returnValue = (HttpEntity<User>)test5.invoke(myController);
        HandlerMethodReturnValueHandlerComposite composite = getHandlerMethodReturnValueHandler();
        HandlerMethod handlerMethod = new HandlerMethod(myController, test5);
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mav = new ModelAndViewContainer();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        if (composite.supportsReturnType(returnType)) {
            /*
                解析HttpEntity返回值[代表整个响应内容]
             */
            composite.handleReturnValue(returnValue, returnType, mav,
                    new ServletWebRequest(request, response));
            if (!mav.isRequestHandled()) {
                // renderView
            } else {
                // do not have to renderView
                // {"id":2,"name":"zhaoliu"}
                System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * 返回值为HttpHeaders的解析流程
     */
    private static void testHttpHeaders() throws Exception{
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Method test6 = MyController.class.getMethod("test6");
        MyController myController = new MyController();
        HttpHeaders returnValue = (HttpHeaders)test6.invoke(myController);
        HandlerMethodReturnValueHandlerComposite composite = getHandlerMethodReturnValueHandler();
        HandlerMethod handlerMethod = new HandlerMethod(myController, test6);
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mav = new ModelAndViewContainer();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        if (composite.supportsReturnType(returnType)) {
            /*
                解析HttpHeaders返回值[代表整个响应头]
             */
            composite.handleReturnValue(returnValue, returnType, mav,
                    new ServletWebRequest(request, response));
            if (!mav.isRequestHandled()) {
                // renderView
            } else {
                // do not have to renderView

                //Content-Type
                response.getHeaderNames().forEach(System.out::println);
                //text/html
                System.out.println(response.getHeader("Content-Type"));
            }
        }
    }

    /**
     * 解析@ResponseBody注解
     */
    private static void testResponseBody() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Method test7 = MyController.class.getMethod("test7");
        MyController myController = new MyController();
        User returnValue = (User)test7.invoke(myController);
        HandlerMethodReturnValueHandlerComposite composite = getHandlerMethodReturnValueHandler();
        HandlerMethod handlerMethod = new HandlerMethod(myController, test7);
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mav = new ModelAndViewContainer();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        if (composite.supportsReturnType(returnType)) {
            /*
                解析@ResponseBody注解[将返回值加入response的响应体中]
                自动生成Content-Type=application/json
             */
            composite.handleReturnValue(returnValue, returnType, mav,
                    new ServletWebRequest(request, response));
            if (!mav.isRequestHandled()) {
                // renderView
            } else {
                // {"id":10,"name":"qianqi"}
                System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * 获取常用返回值处理器组合
     */
    private static HandlerMethodReturnValueHandlerComposite getHandlerMethodReturnValueHandler() {
        HandlerMethodReturnValueHandlerComposite composite = new HandlerMethodReturnValueHandlerComposite();
        composite.addHandlers(
                List.of(
                        // 解析ModelAndView
                        new ModelAndViewMethodReturnValueHandler(),
                        // 解析ViewName -> 将返回值String当作视图名称来解析
                        new ViewNameMethodReturnValueHandler(),
                        // @ModelAttribute[false]或省略@ModelAttribute[true] 该处理器也可以被添加到参数处理器composite中
                        new ServletModelAttributeMethodProcessor(false),
                        // HttpEntity MessageConverter会将HttpEntity转化为json[json实现的消息转换器]
                        new HttpEntityMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
                        // HttpHeader
                        new HttpHeadersReturnValueHandler(),
                        // @ResponseBody[该处理器在参数处理器中解析@RequestBody时也用到] 将返回值作为响应体并根据MessageConverter转换为json
                        new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
                        // @ModelAttribute[false]或省略@ModelAttribute[true] 该处理器也可以被添加到参数处理器composite中
                        new ServletModelAttributeMethodProcessor(true)
                )
        );
        return composite;
    }
}