package edu.seu.deep_in_spring.scope;

import edu.seu.deep_in_spring.scope.bean.invalid.E;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 单例注入多例时，配置失效的问题
 */
@Slf4j
@ComponentScan("edu.seu.deep_in_spring.scope.bean.invalid")
public class TestScopeInvalid {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestScopeInvalid.class);
        E e = context.getBean(E.class);

        // 没有@Lazy注解配合@Autowired时，即使F为prototype，在多次get时也是同一个对象
        /*
            因为单例对象的依赖注入只发生了一次 construct E -> construct F -> e set注入F[@Autowired注解]
         */
        // cglib proxy
        log.debug("{}", e.getF().getClass());

        // F -> prototype 三次调用均为不同对象
        log.debug("{}", e.getF());
        log.debug("{}", e.getF());
        log.debug("{}", e.getF());

        /*
            @Lazy 原理：仍然使用@Lazy生成代理
            代理对象虽然还是同一个，但当每次使用代理对象的任意方法时，由代理创建新的F对象
            construct E -> E set注入F proxy对象 -> 使用F类中方法 -> proxy创建F实例执行该方法
         */
        log.debug("{}", e.getF2());
        log.debug("{}", e.getF2());
        log.debug("{}", e.getF2());
    }
}
