package edu.seu.deep_in_spring_mvc.controllerAdvice;

/**
 * ****@ControllerAdvice注解能对所有控制器提供增强功能[**底层并不是Spring AOP！]
 * 功能增强1. @ExceptionHandler 全局Controller异常处理 2. @ModelAttribute 方法的返回值将作为模型数据补充到控制器中
 * 3.@InitBinder 自定义类型转换器 4.ResponseAdvice + RequestAdvice 对请求和响应进行增强，需要实现相应接口
 * 以上所有功能在@ControllerAdvice类中都是全局的，也可以放在单个Controller中实现对单个Controller的增强功能
 */
public class TestControllerAdvice {

}
