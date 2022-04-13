package edu.seu.deep_in_spring_mvc.returnValueResolver.env;

import edu.seu.deep_in_spring_mvc.requestMappingHandlerMappingAdapter.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class MyController {
    public ModelAndView test1() {
        ModelAndView mav = new ModelAndView("view1");
        mav.addObject("name", "zhangsan");
        return mav;
    }

    public String test2() {
        return "view2";
    }

    @ModelAttribute
    public User test3() {
        log.info("test3");
        return new User(10, "lisi");
    }

    /**
     * 省略@ModelAttribute注解
     * @return 模型数据
     */
    public User test4() {
        log.info("test4");
        return new User(30, "wangwu");
    }

    /*
        上面四种返回值会进行视图渲染，而后面三种返回值会进行MessageConverter不会进行试图渲染
     */

    /**
     * @return 整个http响应
     */
    public HttpEntity<User> test5() {
        log.info("test5");
        return new HttpEntity<>(new User(2, "zhaoliu"));
    }

    /**
     * @return http响应头
     */
    public HttpHeaders test6() {
        log.info("test6");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html");
        return headers;
    }

    /**
     * ResponseBody注解
     */
    @ResponseBody
    public User test7() {
        log.info("test7");
        return new User(10, "qianqi");
    }
}
