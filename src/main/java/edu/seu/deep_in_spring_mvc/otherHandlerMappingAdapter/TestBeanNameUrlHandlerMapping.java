package edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter;

import edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter.env.WebConfig;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

public class TestBeanNameUrlHandlerMapping {
    /**
     * 测试HandlerMapping和HandlerAdapter的另一种实现
     */
    public static void main(String[] args) {
        AnnotationConfigServletWebServerApplicationContext context
                = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
    }
}
