package edu.seu.deep_in_spring_mvc.templateType;

import lombok.Data;
import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 获得泛型参数
 * 1. jdk API
 * 2. Spring API
 */
public class TestTemplateType {
    public static void main(String[] args) {
        // jdk API
        // 获取带有呀范型信息的superClass
        Type type = StudentDao.class.getGenericSuperclass();
        System.out.println(type);

        // 如果type当中有范型
        if (type instanceof ParameterizedType parameterizedType) {
            System.out.println(parameterizedType.getActualTypeArguments()[0]);
        }
        // spring API
        Class<?> aClass = GenericTypeResolver.resolveTypeArgument(StudentDao.class, BaseDao.class);
        System.out.println(aClass);
    }

    static class StudentDao extends BaseDao<Student> {

    }


    static class BaseDao<T> {

    }

    @Data
    static class Student {
        private Integer id;
        private String age;
    }
}
