package edu.seu.deep_in_spring_mvc.controller;

import edu.seu.deep_in_spring_mvc.controller.testEnv.WebConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Spring MVC控制器方法执行流程
 * HandlerMethod = bean[控制器对象] + method[方法对象]
 * ServletInvocableHandlerMethod 继承自HandlerMethod
 * ServletInvocableHandlerMethod = WebDataBinderFactory[数据绑定和类型转换] + ParameterNameDiscover[获取参数名] + HandlerMethodArgumentResolverComposite[参数解析器]
 * + HandlerMethodReturnValueHandlerComposite[返回值处理器]
 */
public class TestController {
    public static void main(String[] args) throws Exception {
        testControllerResolver();
    }

    /*
        RequestMappingHandlerAdapter

        初始化
        1.初始化advice @InitBinder WebDataBinderFactory 自定义类型转换
        2.初始化advice @ModelAttribute ModelFactory 自定义模型工厂
        3.解析@ModelAttribute标注的参数，产生的模型数据放入@ModelAndViewContainer中

        invokeAndHandle -> ServletInvocableHandlerMethod
        4.获取args -> ArgumentResolver + ModelAndViewContainer
        5.****真正执行控制器方法 method.invoke(bean, args) 得到returnValue
        6.返回值处理 -> ReturnValueHandler + ModelAndViewContainer

        7.获取ModelAndView并返回给上层的调用者
     */

    /**
     * 手动构建ServletRequestDataBinderFactory并解析请求，执行控制器方法
     * 手动构建模型工厂[@ControllerAdvice -> @ModelAttribute]
     */
    private static void testControllerResolver() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("name", "张三");

        // 创建HandlerAdapter
        RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
        handlerAdapter.setApplicationContext(context);
        handlerAdapter.afterPropertiesSet();

        // 构建ServletInvocableHandlerMethod[HandlerMethod实现类]
        ServletInvocableHandlerMethod handlerMethod = new ServletInvocableHandlerMethod(new WebConfig.Controller1(),
                WebConfig.Controller1.class.getMethod("foo", WebConfig.User.class));
        // 手动创建类型转换器[不做功能拓展]
        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(null, null);
        handlerMethod.setDataBinderFactory(factory);
        // 设置参数名解析器
        handlerMethod.setParameterNameDiscoverer(new DefaultParameterNameDiscoverer());
        handlerMethod.setHandlerMethodArgumentResolvers(getArgumentResolverComposite(context));

        // 调用invokeAndHandle方法
        ModelAndViewContainer mav = new ModelAndViewContainer();

        // 获取ModelFactory并初始化以解析@ModelAttribute
        Method method = RequestMappingHandlerAdapter.class.getDeclaredMethod(
                "getModelFactory", HandlerMethod.class, WebDataBinderFactory.class);
        method.setAccessible(true);
        ModelFactory modelFactory = (ModelFactory)method.invoke(handlerAdapter, handlerMethod, factory);
        // **** 调用initModel方法，将@ControllerAdvice[全局]或单个控制器[局部]中的@ModelAttribute方法[非参数！]补充到mav中
        modelFactory.initModel(new ServletWebRequest(request), mav, handlerMethod);



        handlerMethod.invokeAndHandle(new ServletWebRequest(request), mav);
        // 获取mav中的model[Map]
        // a=aa, u=WebConfig.User(name=张三)
        System.out.println(mav.getModel());

        context.close();

    }

    /**
     * 获取所有常用参数处理器组合
     */
    private static HandlerMethodArgumentResolverComposite getArgumentResolverComposite(AnnotationConfigApplicationContext context) {
        HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
        composite.addResolvers(
                // beanFactory 获取beanFactory中的后处理器[解析@Value注解等]
                new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), false),
                new PathVariableMethodArgumentResolver(),
                new RequestHeaderMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ServletCookieValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ExpressionValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ServletRequestMethodArgumentResolver(),
                new ServletModelAttributeMethodProcessor(false),
                // JSON @RequestBody
                new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
                new ServletModelAttributeMethodProcessor(true),
                new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), true)
        );
        return composite;
    }
}
