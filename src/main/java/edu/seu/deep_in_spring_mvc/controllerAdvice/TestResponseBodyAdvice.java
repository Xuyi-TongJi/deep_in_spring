package edu.seu.deep_in_spring_mvc.controllerAdvice;

import edu.seu.deep_in_spring_mvc.requestMappingHandlerMappingAdapter.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 消息转换的扩展点：RequestResponseBodyAdvice
 * 场景：一般的响应体中必须要有code，msg，data等消息->可以手动设置Result bean 也可以利用@ControllerAdvice实现
 */
public class TestResponseBodyAdvice {


    @Configuration
    static class WebConfig {
        /**
         * 对响应体增强的@ControllerAdvice 实现ResponseBodyAdvice 对请求体增强的@ControllerAdvice 实现RequestBodyAdvice
         */
        @ControllerAdvice
        static class MyControllerAdvice implements ResponseBodyAdvice<Object> {

            /**
             * 判断是否满足增强条件的逻辑
             */
            @Override
            public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
                // 方法上的@ResponseBody注解
                if (returnType.getMethodAnnotation(ResponseBody.class) != null) {
                    return true;
                }
                // 类[Controller]上的注解 ResponseBody/RestController
                if (returnType.getContainingClass().isAnnotationPresent(ResponseBody.class)) {
                    return true;
                }
                // RestController注解
                return AnnotationUtils.findAnnotation(returnType.getContainingClass(), ResponseBody.class) != null;
            }

            /**
             * 将控制器方法返回值类型转换为Object类型[Result]
             * @param body 控制器方法返回的对象
             * @param returnType 返回值信息
             */
            @Override
            public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                          Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                          ServerHttpRequest request, ServerHttpResponse response) {
                if (body instanceof Result)
                    return body;
                return new Result(200, null,  body);
            }
        }

        @Controller
        static class MyController {
            @ResponseBody
            public User user() {
                return new User(10, "xuyi");
            }
        }

        /**
         * 公共返回对象
         */
        @Data
        @AllArgsConstructor
        static class Result {
            private Integer code;
            private String msg;
            private Object data;
        }
    }
}

