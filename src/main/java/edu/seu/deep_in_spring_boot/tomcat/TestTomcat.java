package edu.seu.deep_in_spring_boot.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

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
     */
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

        // 4. 添加Servlet初始化器，在start()执行后会回调该初始化器以添加Servlet
        context.addServletContainerInitializer(new ServletContainerInitializer() {
            @Override
            public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
                MyServlet myServlet = new MyServlet();
                servletContext.addServlet("myServlet", myServlet).addMapping("/hello");
            }
        }, Collections.emptySet());

        // 5. 启动Tomcat
        tomcat.start();

        // 6. 创建连接器[指定协议和监听端口] --> 通过http1.1<底层为nio2>，端口8080连接tomcat
        Connector connector = new Connector(new Http11Nio2Protocol());
        connector.setPort(8080);
        tomcat.setConnector(connector);
    }

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

    @Configuration
    static class WebConfig {

    }
}
