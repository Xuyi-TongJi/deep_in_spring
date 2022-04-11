package edu.seu.deep_in_spring_mvc.requestMappingHandlerMappingAdapter;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 自定义的参数解析器，可以解析带有@Token注解[自定义]的参数
 */
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 是否支持某个参数[参数上如果有@Token注解，则返回真]
     * @param parameter [控制器]方法参数封装类
     * @return 该解析器是否支持当前参数
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(Token.class) != null;
    }

    /**
     * 参数解析逻辑
     * @param parameter [控制器]方法参数封装类
     * @param mavContainer ?
     * @param webRequest web请求对象
     * @param binderFactory ?
     * @return 这个方法的返回值将会被传递为被解析的控制器方法参数的方法参数值
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return webRequest.getHeader("token");
    }
}
