package edu.seu.deep_in_spring.beanFactoryPostProcessor;

import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.util.Objects;
import java.util.Set;

public class AtBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private final CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();

    @SneakyThrows
    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        MetadataReader reader = factory.getMetadataReader(
                new ClassPathResource("edu/seu/deep_in_spring/beanFactoryPostProcessor/Config.class"));
        Set<MethodMetadata> annotatedMethods = reader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
        for (MethodMetadata method : annotatedMethods) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
            builder.setFactoryMethodOnBean(method.getMethodName(), "config");
            builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
            String initMethod
                    = Objects.requireNonNull(method.getAnnotationAttributes(Bean.class.getName())).get("initMethod").toString();
            if (initMethod != null && initMethod.length() > 0) {
                builder.setInitMethodName(initMethod);
            }
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            if (configurableListableBeanFactory instanceof DefaultListableBeanFactory listableBeanFactory) {
                listableBeanFactory.registerBeanDefinition(method.getMethodName(), beanDefinition);
            }
        }
    }
}
