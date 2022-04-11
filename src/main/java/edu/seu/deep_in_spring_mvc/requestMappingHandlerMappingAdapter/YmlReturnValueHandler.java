package edu.seu.deep_in_spring_mvc.requestMappingHandlerMappingAdapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletResponse;

/**
 * 自定义返回值解析器
 * 可以将返回值解析为Yml格式
 * 功能类似于ResponseBody注解解析器
 */
@Slf4j
public class YmlReturnValueHandler implements HandlerMethodReturnValueHandler {

    /**
     * 是否支持该返回值类型
     * @param returnType 返回值类型封装类
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 得到返回值所在方法上的注解
        Yml yml = returnType.getMethodAnnotation(Yml.class);
        return yml != null;
    }

    /**
     * 处理返回值解析逻辑
     * @param returnValue 返回值 -> 对应控制器的返回对象
     * @param returnType ?
     * @param mavContainer 与springMVC请求处理相关的对象
     * @param webRequest 原始请求+原始相应对象
     */
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 转换返回结果为yml
        String yaml = new Yaml().dump(returnValue);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        // 设置response
        log.info(yaml);
        assert response != null;
        response.setContentType("text/plain;charset=utf-8");
        response.getWriter().print(yaml);

        // 设置请求已经处理完毕，springMVC不用继续处理
        mavContainer.setRequestHandled(true);
    }
}
