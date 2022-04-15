package edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter;

import edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter.env.WebConfig;
import edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter.env.WebConfigR;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

/**
 * RouterFunctionMapping和HandlerFunctionAdapter执行逻辑
 */
public class TestRouterFunctionMappingHandlerFunctionAdapter {
    public static void main(String[] args) {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(WebConfigR.class);
    }
}
