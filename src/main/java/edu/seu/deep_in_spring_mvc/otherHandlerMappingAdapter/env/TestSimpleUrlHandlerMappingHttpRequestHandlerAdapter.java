package edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter.env;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

/**
 * SimpleUrlHandlerMapping
 * HttpRequestHandlerAdapter
 * 可以进行静态资源处理
 * 前者作映射，后者作处理
 *
 * 欢迎页处理[Spring Boot WelcomePageHandlerMapping]
 */
public class TestSimpleUrlHandlerMappingHttpRequestHandlerAdapter {
    public static void main(String[] args) {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(WebConfigS.class);
    }
}
