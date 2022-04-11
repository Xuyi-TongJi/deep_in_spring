package edu.seu.deep_in_spring_mvc.argumentResolver;

import edu.seu.deep_in_spring_mvc.requestMappingHandlerMappingAdapter.pojo.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MyController {

    public void test(
            @RequestParam("name1") String name1, // name1=xxx[url形式或表单形式] 方法参数名与请求参数名匹配
            String name2, // name2=xxx 方法参数名与请求参数名匹配
            /* 以上二者为同一参数解析器解析 */
            @RequestParam("age") Integer age, // age=18 int类型->数据类型转换[参数解析器调用数据绑定对象完成]
            @RequestParam(name = "home", defaultValue = "${JAVA_HOME}") String home, // @RequestParam的defaultValue可以获取非请求中的数据[JVM, Spring]
            @RequestParam("file") MultipartFile file, // 获取上传文件类型参数
            @PathVariable("id") int id, // 路径参数 /{id}
            @RequestHeader("Content-Type") String header, // 获取请求头中的数据
            @CookieValue("token") String token, // Cookie中的数据
            @Value("${JAVA_HOME}") String home2, // 从环境[JVM, Spring]中获取值
            HttpServletRequest request, // 特殊类型参数 request, response, session, cookie
            @ModelAttribute User user1, // name=zhangsan&age=18 定义一个对象进行数据绑定[请求参数中的信息绑定到对象中]
            User user2, // @ModelAttribute注解可以省略[其二者用同一个参数解析起]
            @RequestBody User user3 // 请求体中获取数据 [application/json格式] 常用于POST,PUT,DELETE
            ) {
    }
}
