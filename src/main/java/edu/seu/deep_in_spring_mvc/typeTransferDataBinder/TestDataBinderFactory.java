package edu.seu.deep_in_spring_mvc.typeTransferDataBinder;

import lombok.Data;
import lombok.ToString;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import java.util.Collections;
import java.util.Date;

/**
 * 绑定器工厂
 */
public class TestDataBinderFactory {

    public static void main(String[] args) {

    }

    /**
     * Servlet绑定器工厂 -> 获取dataBinder
     * 支持两种扩展功能的方法：initBinder和ConversionService
     * 也可以使用DefaultConversionService/ApplicationConversionService[Springboot]+@DataFormat注解实现对日期类型的转换
     */
    private static void testServletDataBinderFactory() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("birthday", "1999|01|02");
        request.setParameter("address.name", "西安");

        User target = new User();
        // 设置扩展功能 InitBinder
        InvocableHandlerMethod method =
                new InvocableHandlerMethod(new MyController(), MyController.class.getMethod("binder", WebDataBinder.class));
        // 也可以用ConversionService + 初始化器实现

        // 绑定器工厂 -> 支持扩展 args1 扩展方法
        ServletRequestDataBinderFactory factory = new ServletRequestDataBinderFactory(Collections.singletonList(method), null);


        // 获得绑定器
        WebDataBinder dataBinder = factory.createBinder(new ServletWebRequest(request), target, "user");


        // servlet参数绑定
        dataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(target);
    }

    @Data
    @ToString
    static class User {
        private Date birthday;
        private Address address;
    }

    @Data
    @ToString
    static class Address {
        private String name;
    }

    static class MyController {
        @InitBinder
        public void binder(WebDataBinder webDataBinder) {
            // 扩展DataBinder的转换器

            /*  日期类型转换代码 -> 实现Formatter接口  */
        }
    }
}
