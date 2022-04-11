package edu.seu.deep_in_spring_mvc.typeTransferDataBinder;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;

import java.util.Date;

/**
 * 类型转换的两个底层接口:ConversionService[Spring],PropertyEditorRegistry[JDK]
 * 测试：Spring类型转换高层接口的四个实现
 */
public class TestTypeTransfer {
    public static void main(String[] args) {
        //testSimpleTypeConverter();
        testBeanWrapper();
    }

    /**
     * SimpleTypeConverter仅有类型转换的功能
     */
    private static void testSimpleTypeConverter() {
        SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter();
        Integer number = simpleTypeConverter.convertIfNecessary("13", Integer.class);
        Date date = simpleTypeConverter.convertIfNecessary("1999/03/04", Date.class);
        System.out.println(number);
        System.out.println(date);
    }

    /**
     * 用反射原理给Bean的成员变量赋值[并进行类型转换]
     * 赋值底层调用getter和setter
     */
    private static void testBeanWrapper() {
        MyBean myBean = new MyBean();
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(myBean);
        beanWrapper.setPropertyValue("a", "10");
        beanWrapper.setPropertyValue("b", "hello");
        beanWrapper.setPropertyValue("c", "1999/03/04");
        System.out.println(myBean);
        // TestTypeTransfer.MyBean(a=10, b=hello, c=Thu Mar 04 00:00:00 CST 1999)
    }

    /**
     * fieldAccessor也可以实现为成员变量赋值的功能[并实现类型转换]
     * 但底层不是调用getter和setter，底层调用成员变量
     */
    private static void testFieldAccessor() {
        // ..
    }

    /**
     * 使用数据绑定器实现为成员变量赋值
     * 支持getter和setter方法，也可以通过设置支持private成员变量
     */
    private static void testDataBinder() {
        MyBean target = new MyBean();
        DataBinder dataBinder = new DataBinder(target);
        MutablePropertyValues pvs = new MutablePropertyValues();
        // 支持private成员变量
        dataBinder.initDirectFieldAccess();
        pvs.add("a", "10");
        pvs.add("b", "hello");
        pvs.add("c", "1999/03/04");
        dataBinder.bind(pvs);
        System.out.println(target);
    }

    /**
     * ****适合web环境下使用，在需要绑定请求参数时，需要使用dataBinder的子类实现
     * ModelAttribute参数解析器的底层原理
     */
    private static void testServletDataBinder() {
        MyBean target = new MyBean();
        ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(target);

        // test request
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("a", "10");
        request.setParameter("b", "hello");
        request.setParameter("c", "`1999/03/04");

        // 将request中的数据绑定到target中
        dataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(target);
    }

    @Data
    @ToString
    static class MyBean {
        private int a;
        private String b;
        private Date c;
    }
}
