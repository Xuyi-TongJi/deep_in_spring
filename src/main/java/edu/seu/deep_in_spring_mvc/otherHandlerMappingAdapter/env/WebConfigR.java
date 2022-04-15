package edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter.env;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.function.*;
import org.springframework.web.servlet.function.support.HandlerFunctionAdapter;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

@Configuration
public class WebConfigR {
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
     * 函数式请求映射器
     */
    @Bean
    public RouterFunctionMapping routerFunctionMapping() {
        return new RouterFunctionMapping();
    }

    /**
     * 函数式请求适配器
     */
    @Bean
    public HandlerFunctionAdapter handlerFunctionAdapter() {
        return new HandlerFunctionAdapter();
    }

    /**
     * 请求映射+处理函数
     */
    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route(RequestPredicates.GET("/r1"), new HandlerFunction<ServerResponse>() {
            /**
             * 给定请求对象，返回响应对象[请求处理逻辑，可执行响应状态码，响应体，响应头等]
             * @param request 请求对象
             * @return 响应对象
             */
            @Override
            public ServerResponse handle(ServerRequest request) throws Exception {
                return ServerResponse.ok().body("this is r1 handling");
            }
        });
    }
}

