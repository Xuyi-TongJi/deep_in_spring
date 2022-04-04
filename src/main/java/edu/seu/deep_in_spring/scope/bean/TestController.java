package edu.seu.deep_in_spring.scope.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
/*
    autowireConstructor 注入: @RequiredAllArgsConstructor
    补充：依赖注入和初始化方法执行顺序：
    constructor[构造方法注入] --> @Autowired @Resource[扩展功能注入] --> [对象未实例化]|| [对象已实例化]  @postConstruct --> InitializingBean --> initMethod

    这个case不能使用构造方法注入 -> 只有被注入和注入的Bean都为单例时才可以使用构造方法注入
    否则只能使用@Autowired + @Lazy实现代理注入
 */
public class TestController {

    /**
     * TestController是一个singleton，注入其他四种域对象，必须是@Lazy[代理注入]
     */
    @Lazy
    @Autowired
    private BeanForRequest beanForRequest;

    @Lazy
    @Autowired
    private BeanForSession beanForSession;

    @Lazy
    @Autowired
    private BeanForApplication beanForApplication;

    @GetMapping(value = "/test", produces = "text/html")
    public String test(HttpServletRequest request, HttpServletResponse response) {
        ServletContext context = request.getServletContext();
        /*
            每次请求[刷新浏览器模拟]，beanForRequest都是不同对象，而另二者相同
            同一个浏览器无论发送多少次请求，都属于一个会话，所欲beanForSession为同一对象
            同一个web应用程序只有一个ServletContext容器，所以beanForApplication为同一对象
            即使是不同浏览器，不同主机，beanForApplication为同一对象
         */
        return "beanForRequest:" + beanForRequest + '\n' +
                "beanForSession" + beanForSession + '\n' +
                "beanForApplication" + beanForApplication;
    }
}
