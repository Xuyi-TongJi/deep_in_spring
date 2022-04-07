package edu.seu.deep_in_spring_mvc.dispatcherServlet;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 内嵌web容器的ApplicationContext必须有：内嵌的web容器工厂、DispatcherServlet以及SpringMVC入口[DispatcherServlet注册]
 */
@Configuration
@ComponentScan
/*
  读取配置文件
 */
@PropertySource("classpath:application.properties")
/*
  创建两个Bean，这两个Bean的属性绑定了配置文件中的属性
 */
@EnableConfigurationProperties({WebMvcProperties.class, ServerProperties.class})
public class WebConfig {
    /**
     * 内嵌web容器[Tomcat实现]
     * 通过配置类ServerProperties进行配置[@EnableConfigurationProperties将自动创建配置类Bean，并将配置文件中的相应属性值注入到该配置类Bean]
     */
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(ServerProperties serverProperties) {
        return new TomcatServletWebServerFactory(serverProperties.getPort());
    }

    /**
     * DispatcherServlet
     */
    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    /**
     * SpringMVC入口，注册DispatcherServlet
     * 参数按类型匹配注入
     */
    @Bean
    public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet,
                                                                               WebMvcProperties webMvcProperties) {
        DispatcherServletRegistrationBean registrationBean
                = new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        // 设置饥饿初始化，在tomcat启动时就对DispatcherServlet进行初始化[一般在配置文件中定义]
        int loadOnStartup = webMvcProperties.getServlet().getLoadOnStartup();
        registrationBean.setLoadOnStartup(loadOnStartup);
        return registrationBean;
    }
}
