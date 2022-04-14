package edu.seu.deep_in_spring_mvc.exceptionHandlerTomcat;

import edu.seu.deep_in_spring_mvc.exceptionHandlerTomcat.env.WebConfig;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class TestExceptionHandlerTomcat {
    public static void main(String[] args) {
        testTomcatExceptionHandler();
    }

    private static void testTomcatExceptionHandler() {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        RequestMappingHandlerMapping handlerMapping
                = context.getBean(RequestMappingHandlerMapping.class);
        handlerMapping.getHandlerMethods().forEach((RequestMappingInfo k, HandlerMethod m) -> {
            //	e.s.d.e.e.MyController:
            //	{ [/test]}: test()
            System.out.println("mapping:" + k + "\t" + "method:" + m);
        });
    }
}
