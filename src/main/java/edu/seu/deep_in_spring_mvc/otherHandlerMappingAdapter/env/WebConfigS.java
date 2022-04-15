package edu.seu.deep_in_spring_mvc.otherHandlerMappingAdapter.env;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.List;
import java.util.Map;

@Configuration
public class WebConfigS {

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
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

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping(ApplicationContext applicationContext) {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        // 获取映射关系 BeanName --> ResourceHttpRequestHandler
        Map<String, ResourceHttpRequestHandler> map = applicationContext.getBeansOfType(ResourceHttpRequestHandler.class);
        simpleUrlHandlerMapping.setUrlMap(map);
        return simpleUrlHandlerMapping;
    }

    /**
     * 负责调用ResourceHttpRequestHandler的适配器
     */
    @Bean
    public HttpRequestHandlerAdapter httpRequestHandlerAdapter() {
        return new HttpRequestHandlerAdapter();
    }

    /**
     * 静态资源请求处理器配置
     * 可以设置静态资源的目录
     * Bean名称为uri
     *
     * 可自定义其资源解析器[获得增强]
     */
    @Bean("/**")
    public ResourceHttpRequestHandler resourceHttpRequestHandler1() {
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        handler.setLocations(List.of(new ClassPathResource("static/")));
        // 自定义资源解析器 -> 指责链模式
        handler.setResourceResolvers(
                List.of(
                        // 读取资源时加入缓存功能
                        new CachingResourceResolver(new ConcurrentMapCache("cache")),
                        // 读取缓存资源
                        new EncodedResourceResolver(),
                        // 根据路径读取资源
                        new PathResourceResolver()
                )
        );
        return handler;
    }

    @Bean("/tmp/**")
    public ResourceHttpRequestHandler resourceHttpRequestHandler2() {
        ResourceHttpRequestHandler handler = new ResourceHttpRequestHandler();
        handler.setLocations(List.of(new ClassPathResource("templates/")));
        return handler;
    }

    /**
     * 欢迎页处理
     */
/*    @Bean
    public WelcomePageHandlerMapping welcomePageHandlerMapping() {

    }*/
}
