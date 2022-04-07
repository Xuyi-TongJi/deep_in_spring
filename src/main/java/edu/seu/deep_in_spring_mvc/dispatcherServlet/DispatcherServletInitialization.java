package edu.seu.deep_in_spring_mvc.dispatcherServlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;

/**
 * DispatcherServlet的初始化时机
 * [DispatcherServlet对象的实例化由Spring@Bean创建，但[默认情况下]SpringBean的初始化在其首次被使用时，由Tomcat容器创建]
 * [DispatcherServlet走Servlet的生命周期]
 * 信息: Initializing Spring DispatcherServlet 'dispatcherServlet'
 * 19:58:48.062 [http-nio-8080-exec-1] INFO org.springframework.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'
 *
 * DispatcherServlet初始化方法： DispatcherServlet::onRefresh(ApplicationContext context)
 * 在第一次对tomcat发送请求时触发它的初始化[初始化各流程，Servlet的初始化流程]
 * 默认为懒惰初始化，可以设置使其饥饿初始化[一般在配置文件中定义]
 *
 * DispatcherServlet的初始化过程就是初始化一些组件，然后将其放入到DispatcherServlet相应成员变量[容器]中
 */
@Slf4j
public class DispatcherServletInitialization {
    public static void main(String[] args) {
        // 支持内嵌web容器的context
        AnnotationConfigServletWebServerApplicationContext context
                = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
    }

    /**
     * DispatcherServlet初始化时的具体操作[DispatcherServlet的内部组件]
     * 源码：DispatcherServlet::initStrategies
     *     protected void initStrategies(ApplicationContext context) {
     *         初始化文件上传解析器 Multipart
     *         this.initMultipartResolver(context);
     *         初始化本地信息 [国际化功能]
     *         this.initLocaleResolver(context);
     *         this.initThemeResolver(context);
     *         [Handler] 具体执行处理请求的那段代码 --> Handler
     *         [重要]初始化路径映射器组件[@RequestMapping]，处理该请求具体由哪一个控制器方法处理
     *         this.initHandlerMappings(context);
     *         ****如果容器中没有提供HandlerMapping，则会使用DispatcherServlet.properties中默认的给类组件
     *         [将相应组件放入到DispatcherServlet的相应组件List中]，其他组件也是类似原理****
     *         [重要]要执行控制器方法，必须要做适配 -> 适配不同形式的控制器方法，然后真正调用控制器方法的组件为HandlerAdaptor
     *         this.initHandlerAdapters(context);
     *
     *         [重要]解析异常处理器
     *         this.initHandlerExceptionResolvers(context);
     *         this.initRequestToViewNameTranslator(context);
     *         this.initViewResolvers(context);
     *         this.initFlashMapManager(context);
     *
     *         [重要]
     *         这些组件只是作为DispatcherServlet Bean的成员变量，并没有放入Spring容器[不是Spring Bean，该过程也非依赖注入过程]
     *     }
     */

}
