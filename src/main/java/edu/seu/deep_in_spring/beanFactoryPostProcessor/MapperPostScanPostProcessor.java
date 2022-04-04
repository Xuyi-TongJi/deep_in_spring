package edu.seu.deep_in_spring.beanFactoryPostProcessor;

import lombok.SneakyThrows;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

/**
 * 自定义BeanFactoryPostProcessor后处理器，实现对@MapperScan方法的解析
 */
public class MapperPostScanPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @SneakyThrows
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources
                = resolver.getResources("classpath:edu/seu/deep_in_spring/beanFactoryPostProcessor/component/**/*.class");
        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
        for (Resource resource : resources) {
            MetadataReader reader = factory.getMetadataReader(resource);
            ClassMetadata classMetadata = reader.getClassMetadata();
            AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
            // Mapper一定是接口 且 该接口必须含有@Mapper注解，才会被解析并加入BeanFactory
            if (classMetadata.isInterface() && reader.getAnnotationMetadata().hasAnnotation(Mapper.class.getName())) {
                // 被Spring管理的类（Bean）是Mapper工厂
                AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MapperFactoryBean.class)
                        // MapperFactoryBean<Mapper1>
                        .addConstructorArgValue(classMetadata.getClassName())
                        // 对于参数SqlSessionFactory的传递，采用类型自动注入【从容器中找到相同类型的Bean传参】
                        .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                        .getBeanDefinition();

                // 根据接口生成一个BeanDefinition，用于生成真正注入容器的BeanDefinition(MapperFactoryBean)的beanName
                AbstractBeanDefinition nameDefinition
                        = BeanDefinitionBuilder.genericBeanDefinition(classMetadata.getClassName()).getBeanDefinition();
                String name = generator.generateBeanName(nameDefinition, beanDefinitionRegistry);
                // 将BeanDefinition注册到BeanFactory
                beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
