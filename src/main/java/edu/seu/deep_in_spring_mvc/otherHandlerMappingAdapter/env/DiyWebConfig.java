package edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter.env;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.Controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class DiyWebConfig {
    @Bean
    public TomcatServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean
    public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet) {
        return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
    }

    /**
     * 自定义HandlerMapping
     * 逻辑：找到与请求URI名称相同的Controller Bean进行处理
     */
    @Component
    static class MyHandlerMapping implements HandlerMapping {

        @Override
        public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
            String key = request.getRequestURI();
            Controller controller = collect.get(key);
            // 没有控制器可以处理
            if (controller == null) {
                return null;
            }
            // 返回处理器链 -> controller + interceptor
            return new HandlerExecutionChain(controller);
        }

        @Autowired
        private ApplicationContext applicationContext;

        private Map<String, Controller> collect;

        @PostConstruct
        public void init() {
            // 找到所有以'/'开头的Bean
            Map<String, Controller> controllers =
                    applicationContext.getBeansOfType(Controller.class);
            collect = controllers.entrySet().stream().filter(e -> e.getKey().startsWith("/"))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    @Component
    static class MyHandlerAdapter implements HandlerAdapter {

        /**
         * 判断是否可以处理该请求只需要判断当前handler是否为Controller
         * @param handler 请求处理对象[Controller]
         */
        @Override
        public boolean supports(Object handler) {
            return handler instanceof Controller;
        }

        /**
         * 执行控制器方法
         * @param request 请求
         * @param response 响应
         * @param handler 请求处理对象[Controller]
         * @return ModelAndView 返回null则不会进行视图渲染
         * @throws Exception
         */
        @Override
        public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if (handler instanceof Controller controller) {
                controller.handleRequest(request, response);
            }
            return null;
        }

        @Override
        public long getLastModified(HttpServletRequest request, Object handler) {
            return -1;
        }
    }

    @Component("/c1")
    public static class Controller1 implements Controller {

        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.getWriter().print("C1 is handling");
            return null;
        }
    }
}
