package edu.seu.deep_in_spring.scope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring5 中的五种Scope
 * singleton prototype request session application
 * singleton 单例
 * prototype 每次获取，创建一个新的对象
 * request Bean存在于Request域 请求开始时，会创建Bean到Request域中，请求结束，Bean销毁
 * session Bean存在于Session域 会话开始创建，会话结束销毁
 * application 存在于Web ServletContext域中
 * 单例的创建时机：懒惰/饥饿创建, 在Spring容器销毁时销毁
 * prototype的创建时机：每次使用时，销毁不会由Spring容器管理，可以自行调用每个对象的销毁方法
 */
@SpringBootApplication
@ComponentScan("edu.seu.deep_in_spring.scope.bean")
public class TestScope {
    public static void main(String[] args) {
        SpringApplication.run(TestScope.class);
    }
}