package edu.seu.deep_in_spring_mvc.argumentResolver;

import edu.seu.deep_in_spring_mvc.requestMappingHandlerMappingAdapter.pojo.User;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * springMVC 参数解析器 [HandlerAdapter中的重要组件]
 * 参数解析器[组合模式] -> supportsParameter resolveArguments
 */
public class TestArgumentResolver {
    public static void main(String[] args) throws Exception {

        // 无需web环境
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext(WebConfig.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        HttpServletRequest request = mockRequest();

        /* 构造控制器方法HandlerMethod[HandlerMapping的功能 -> 将Controller中的每一个方法封装成HandlerMethod] */

        // arg1 控制器对象 arg2 控制器方法对象
        HandlerMethod handlerMethod
                = new HandlerMethod(new MyController(), MyController.class.getMethod("test", String.class, String.class, Integer.class, String.class, MultipartFile.class, int.class, String.class, String.class, String.class, HttpServletRequest.class, User.class, User.class, User.class));

        /* 准备对象绑定与类型转换 */

        /* 准备ModelAndViewContainer 用来存储中间Model结果 */
        ModelAndViewContainer modelAndViewContainer = new ModelAndViewContainer();

        // RequestParam参数解析器
        // arg1: beanFactory 从beanFactory后处理器中可以解析@Value ${}等注解
        // arg2: 能否省略@RequestParam注解[原生的@RequestParam参数解析起无类型转换功能，无${}解析功能]
/*        RequestParamMethodArgumentResolver requestParamMethodArgumentResolver
                = new RequestParamMethodArgumentResolver(beanFactory, true);*/

        // 组合模式添加多个解析器
        HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
        composite.addResolvers(
                // @RequestParam false 表示不能省略该注解
                new RequestParamMethodArgumentResolver(beanFactory, false),
                // @PathVariable
                new PathVariableMapMethodArgumentResolver(),
                new RequestHeaderMethodArgumentResolver(beanFactory),
                new ServletCookieValueMethodArgumentResolver(beanFactory),
                // @Value
                new ExpressionValueMethodArgumentResolver(beanFactory),
                // request, response 等 JavaWeb对象
                new ServletRequestMethodArgumentResolver(),
                // @ModelAttribute true代表可以省略该注解
                new ModelAttributeMethodProcessor(false),
                // @RequestBody
                new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),

                // 顺序与@RequestBody注解解析器不能调换
                new ModelAttributeMethodProcessor(true),
                // 该解析器必须放在最后[省略@RequestParam的解析器]
                new RequestParamMethodArgumentResolver(beanFactory, true)
                );


        /* 解析每个参数值 */
        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            // 获取每个参数上的注解
            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
            String annotations = Arrays.stream(methodParameter.getParameterAnnotations())
                    .map(a -> a.annotationType().getSimpleName()).collect(Collectors.joining());

            DefaultDataBinderFactory binderFactory = new DefaultDataBinderFactory( null);
            if (composite.supportsParameter(methodParameter)) {
                // 使用参数解析器解析参数 返回参数值

                // arg3 : 数据绑定工厂 -> 可以实现类型转换[eg: String -> Integer]
                Object argument = composite.resolveArgument(methodParameter, modelAndViewContainer,
                        new ServletWebRequest(request), binderFactory);
                System.out.println("[" + methodParameter.getParameterIndex()
                        + methodParameter.getParameterType().getSimpleName()
                        + methodParameter.getParameterName() + "---->" + argument + "]");
                System.out.println("@" + annotations);
            } else {
                System.out.println("[" + methodParameter.getParameterIndex()
                        + methodParameter.getParameterType().getSimpleName()
                        + methodParameter.getParameterName() + "]");
                System.out.println("@" + annotations);
                System.out.print("\n");
            }
        }
    }

    /**
     * 设置测试Request对象及其参数
     */
    private static HttpServletRequest mockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("name1", "zhangsan");
        request.setParameter("name2", "lisi");
        request.setParameter("id", "16");
        // MultiPartFile
        request.addPart(new MockPart("file", "abc", "hello".getBytes(StandardCharsets.UTF_8)));
        // PathVariable
        Map<String, String> uriTemplateVariables = new AntPathMatcher().extractUriTemplateVariables("/test/{id}", "/test/123");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);

        // request.addHeader("Content-Type", "multipart/form-data") -> 为了测试MultiPart参数，必须将Content-Type
        request.setContentType("multipart/form-data");
        request.setCookies(new Cookie("token", "123456"));
        request.setParameter("name", "zhangsan");
        request.setParameter("age", "18");

        return request;
    }
}