package edu.seu.deep_in_spring_mvc.controller.testEnv;

import lombok.Data;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class WebConfig {
    @Controller
    public static class Controller1 {
        /**
         * 暂时不考虑返回值处理器
         * 注解@ModelAttribulte通过参数解析起来解析[可省略/不可省略]，解析完成后作为模型数据放入mav中
         * 没有指定名称，则将对象首字母小写作为名称
         */
        @ResponseStatus(HttpStatus.OK)
        public ModelAndView foo(@ModelAttribute("u") User user) {
            System.out.println("foo");
            return null;
        }
    }

    @ControllerAdvice
    public static class MyControllerAdvice {
        /**
         * 如果ModelAttribute加在@ControllerAdivce上，作用是将一些模型数据补充到mav中
         * 解析器并非是参数解析器，而由RequestMappingHandlerAdapter负责解析
         * 没有指定类型，则将返回值类型小写作为名称存入mav中
         */
        @ModelAttribute("a")
        public String aa() {
            return "aa";
        }
    }

    @Data
    @ToString
    public static class User {
        private String name;
    }
}
