package edu.seu.deep_in_spring.beanFactoryPostProcessor;

import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

public class MyComponentScanPostProcessor implements BeanFactoryPostProcessor {

    private final CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
    private final AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();

    /**
     * 在context.refresh()时调用该方法,实现对@ComponentScan的后处理
     *
     * @param beanFactory configurableListableBeanFactory[listableBeanFactory的父接口]
     */
    @SneakyThrows
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ComponentScan annotation = AnnotationUtils.findAnnotation(Config.class, ComponentScan.class);
        if (annotation != null) {
            for (String p : annotation.basePackages()) {
                String path = "classpath*:" + p.replace(".", "/") + "/**/*.class";
                Resource[] resources = new PathMatchingResourcePatternResolver().getResources(path);
                for (Resource resource : resources) {
                    MetadataReader reader = factory.getMetadataReader(resource);
                    /* if the resource has the annotation of @Component and its subclass, then add to the beanFactory*/
                    if (reader.getAnnotationMetadata().hasAnnotation(Component.class.getName())
                            || reader.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName())) {
                        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                                .genericBeanDefinition(reader.getClassMetadata().getClassName()).getBeanDefinition();
                        if (beanFactory instanceof DefaultListableBeanFactory listableBeanFactory) {
                            String name = generator.generateBeanName(beanDefinition, listableBeanFactory);
                            listableBeanFactory.registerBeanDefinition(name, beanDefinition);
                        }
                    }
                }
            }
        }
    }
}
