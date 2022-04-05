package edu.seu.deep_in_spring.aop;

import edu.seu.deep_in_spring.aop.service.MyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * AOP最典型的实现：代理实现
 * AOP可以在字节码生成[编译]阶段，类加载阶段代理增强
 */
@Slf4j
@SpringBootApplication
public class TestAop {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TestAop.class);
        MyService myService = context.getBean(MyService.class);
        log.info("service class:{}", myService.getClass());
        myService.foo();
        context.close();
    }
}