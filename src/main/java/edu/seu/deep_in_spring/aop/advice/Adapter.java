package edu.seu.deep_in_spring.aop.advice;

/**
 * 将四种其他类型的通知转换为环绕通知时所使用的类
 * MethodBeforeAdviceAdapter将@Before AspectJMethodBeforeAdvice 适配为 MethodBeforeAdviceInterceptor
 * AfterReturningAdviceAdapter将 AspectJAfterReturningAdvice 适配为 AfterReturningAdviceInterceptor
 *
 * 适配器中的两个核心方法：supportsAdvice 表明适配器需要适配哪种通知， getInterceptor 将通知逻辑以AdviceInterceptor的形式实现
 */
public class Adapter {

}