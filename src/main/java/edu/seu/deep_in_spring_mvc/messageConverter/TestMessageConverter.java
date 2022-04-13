package edu.seu.deep_in_spring_mvc.messageConverter;

import edu.seu.deep_in_spring_mvc.messageConverter.env.Person;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 消息转换器:将请求体，响应体中的其他数据格式的数据[比如json,XML, 根据Converter的具体实现]和java对象互相转换
 * 在参数处理器，返回值处理器中的RequestResponseBodyMethodProcessor使用到
 */
public class TestMessageConverter {
    public static void main(String[] args) throws HttpMediaTypeNotAcceptableException, IOException, NoSuchMethodException {
        test2();
    }

    /**
     * 消息转换为json对象
     */
    private static void test1() throws IOException {
        MockHttpOutputMessage message = new MockHttpOutputMessage();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        if (converter.canWrite(Person.class, MediaType.APPLICATION_JSON)) {
            converter.write(new Person(10, "xuyi"), MediaType.APPLICATION_JSON, message);
            System.out.println(message.getBodyAsString());
        }
    }

    /**
     * 多个消息转换器之间的协作
     */
    private static void test2() throws NoSuchMethodException, HttpMediaTypeNotAcceptableException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);

        // 优先级1：如果response中有Content-Type头，则优先转换为该类型
        // @RequestMapping(produces="application/json")
        response.addHeader("Content-Type", "application/json");

        // 优先级2：****如果有该设置，则优先转换为application/json
        request.addHeader("Accept", "application/json");

        // 参数/返回值解析器RequestResponseBodyMethodProcessor, 构造时需要添加消息转换器
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(
                List.of(new MappingJackson2HttpMessageConverter(),
                        new MappingJackson2XmlHttpMessageConverter())
        );
        // 模拟返回值处理
        processor.handleReturnValue(
                // return value
                new Person(10, "dfsa"),
                // method parameter @ResponseBody
                new MethodParameter(TestMessageConverter.class.getMethod("person"), -1),
                new ModelAndViewContainer(),
                servletWebRequest
        );
        // 优先级3：最终转换结果如果未作处理[request不含有Accept头]，则按照构造processor时添加converter的数据决定
        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    @ResponseBody
    public Person person() {
        return null;
    }

}
