package edu.seu.deep_in_spring_boot.autoConfiguration;

import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * 条件装配@ConditionOn/@Conditional("xxx")注解底层
 */
public class TestConditionOn {
    public static void main(String[] args) {

    }

    @Configuration
    static class MyConfig {

    }

    static class MySelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{AutoConfiguration1.class.getName(), AutoConfiguration2.class.getName()};
        }
    }

    /**
     * 实现Condition接口，配合@Conditional注解使用可以实现条件装配
     *
     * 在自定义的@ConditionOnClass注解中，获取注解信息以进行条件装配判断
     */
    static class MyCondition1 implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // 使用metadata[AnnotatedTypeMetadata]获取@ConditionOnClass注解的属性
            Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionOnClass.class.getName());
            String className = annotationAttributes.get("className").toString();
            return ClassUtils.isPresent(className, null) ^ (boolean)annotationAttributes.get("exists");
        }
    }

    static class MyCondition2 implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // 类路径下没有Druid.class则条件成立
            return !ClassUtils.isPresent("com.alibaba.druid.pool.DruidDataSource", null);
        }
    }

    /**
     * 自定义注解实现@Conditional
     * 判断某一类[className]是否存在或是否不存在[exists]以进行条件装配
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    // 组合注解 -> 底层是@Conditional + 实现了Condition的类，这个类中有与该注解相关的判断逻辑
    @Conditional(MyCondition1.class)
    @interface ConditionOnClass {
        // true存在，false不存在
        boolean exists();
        // 要判断的类名
        String className();
    }


    @Configuration
    //@Conditional(MyCondition1.class)
    @ConditionOnClass(exists = false, className = "com.alibaba.druid.pool.DruidDataSource")
    static class AutoConfiguration1 {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }
    }

    @Configuration
    @Conditional(MyCondition2.class)
    static class AutoConfiguration2 {
        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean1 {
        public static final Long ID = 1L;
    }

    static class Bean2 {
        public static final Long ID = 2L;
    }
}
