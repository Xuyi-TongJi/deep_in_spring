package edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter;

import edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter.env.DiyWebConfig;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

/**
 * 自定义实现HandlerMapping和HandlerAdapter
 */
public class TestDiyHandlerMappingAdapter {
    public static void main(String[] args) {
        AnnotationConfigServletWebServerApplicationContext context
                = new AnnotationConfigServletWebServerApplicationContext(DiyWebConfig.class);
    }
}
