package edu.seu.deep_in_spring_boot.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 内嵌Tomcat容器
 * 内嵌容器的基本使用
 * 内嵌容器加载DispatcherServlet的时机
 */
public class TestTomcat {
    public static void main(String[] args) throws LifecycleException, IOException {
        /*
            tomcat能直接识别的只有三大组件[Servlet, Filter, Listener]
            [必须在web.xml中进行配置, 3.0版本后可以通过编程方式代替xml方式，***内嵌容器通过编程方式实现]
            controller, service只能通过三大组件间接地被调用才能被tomcat识别

            两个路径
            每个Context[Tomcat应用]必须配置虚拟路径[可以设置为"/"]，即url起始路径，但每个应用必须有不同的虚拟路径
            每个Context必须配置一个磁盘路径，即docBase
         */
        createTomcat();
    }

    /**
     * 通过编程方式创建内嵌Tomcat容器
     *
     * SpringBoot的run方法中，先创建spring容器，在spring容器的onRefresh()方法执行时，会创建Tomcat对象
     */
    @SuppressWarnings("all")
    private static void createTomcat() throws IOException, LifecycleException {
        // 1. 创建Tomcat对象
        Tomcat tomcat = new Tomcat();
        // 相对路径创建基础目录
        tomcat.setBaseDir("tomcat");

        // 2. 创建项目文件夹，即docBase文件夹
        File docBase = Files.createTempDirectory("boot.").toFile();
        docBase.deleteOnExit();

        // 3. 创建Tomcat Context [设置两个路径]
        Context context = tomcat.addContext("", docBase.getAbsolutePath());

        // 3.1 获取spring容器 applicationContext[区别于Tomcat项目Context]
        WebApplicationContext applicationContext = getWebApplicationContext(WebConfig.class);

        // 4. 添加Servlet初始化器，在start()执行后会回调该初始化器以添加Servlet
        context.addServletContainerInitializer(new ServletContainerInitializer() {
            @Override
            public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
                // 自定义的Servlet类
                MyServlet myServlet = new MyServlet();
                servletContext.addServlet("myServlet", myServlet).addMapping("/hello");

                // 从spring容器中获取所有Servlet注册Bean[包括dispatcherServlet],Mapping即为Spring容器中dispatcherServlet注册Bean定义的路径
                Map<String, ServletRegistrationBean> registrationBean = applicationContext.getBeansOfType(ServletRegistrationBean.class);
                // 遍历map.values()集合，将所有注册bean中的信息[mapping, servlet]注册到tomcat的context
                for (ServletRegistrationBean servletRegistration : registrationBean.values()) {
                    servletRegistration.onStartup(servletContext);
                }
            }
        }, Collections.emptySet());

        // 5. 启动Tomcat
        tomcat.start();

        // 6. 创建连接器[指定协议和监听端口] --> 通过http1.1<底层为nio2>，端口8080连接tomcat
        Connector connector = new Connector(new Http11Nio2Protocol());
        connector.setPort(8080);
        tomcat.setConnector(connector);
    }

    @SuppressWarnings("all")
    private static WebApplicationContext getWebApplicationContext(Class<?> webConfig) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(webConfig);
        context.refresh();
        return context;
    }

    static class MyServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().print(
                    "<h3>hello</h3>"
            );
        }
    }

    /**
     * 自己配置的内嵌Tomcat配合spring容器使用
     */
    @Configuration
    static class WebConfig {
        @Bean
        public DispatcherServlet dispatcherServlet(WebApplicationContext webApplicationContext) {
            // 必须提供DispatcherServlet,否则会选择XmlWebApplicationContext
            return new DispatcherServlet(webApplicationContext);
        }

        @Bean
        public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet) {
            return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        }

        @Bean
        public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
            RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
            adapter.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
            return adapter;
        }

        /**
         * 只要tomcat中dispatcherServlet并且在spring容器中有dispatcherServlet注册Bean，controller组件就能正常工作
         */
        @RestController
        static class MyController {
            @GetMapping("hello2")
            public Map<String, Object> hello() {
                return Map.of("hello2::", "hello, spring");
            }
        }
    }
}