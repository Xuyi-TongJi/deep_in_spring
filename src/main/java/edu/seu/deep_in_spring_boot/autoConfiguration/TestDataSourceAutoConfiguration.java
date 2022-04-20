package edu.seu.deep_in_spring_boot.autoConfiguration;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * DataSource相关[DataSource, MyBatis]以及事物自动配置
 */
public class TestDataSourceAutoConfiguration {
    public static void main(String[] args) {
        testDataSourceTransactionAutoConfiguration();
    }

    private static void testDataSourceTransactionAutoConfiguration() {
        GenericApplicationContext context = new GenericApplicationContext();
        StandardEnvironment env = new StandardEnvironment();
        // datasource配置
        env.getPropertySources().addLast(
                new SimpleCommandLinePropertySource(
                        "--spring.datasource.url=jdbc:mysql://localhost:3306/test_spring",
                        "--spring.datasource.username=root",
                        "--spring.datasource.password=Woshij8dan!"
                )
        );
        // 确定Mapper扫描范围
        AutoConfigurationPackages.register(context.getDefaultListableBeanFactory(), TestDataSourceAutoConfiguration.class.getPackageName());

        context.setEnvironment(env);

        // 注入常用后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
        context.registerBean(Config.class);
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            // 来源为null的一般为Configuration或spring内部的后处理器等[工具人]

            // 过滤掉来源为null的bean
            String source = context.getBeanDefinition(name).getResourceDescription();
            if (source != null) {
                System.out.println(name + "----------" + source);
            }
        }

        // DataSourceProperties 封装环境变量中与spring.datasource有关的键值信息
        /*
            @ConfigurationProperties(prefix = "spring.datasource")
            public class DataSourceProperties ...
         */
        // 该类被创建的时机:
        /*
            @EnableConfigurationProperties({DataSourceProperties.class})
            public class DataSourceAutoConfiguration

            @EnableConfigurationProperties注解：支持相应类的绑定功能。如果该注解所包含的class未在spring容器中，则会创建一个相应的Bean放入容器中
            使用：在创建dataSource时，如果需要用到相应信息，则会到该配置信息类DataSourceProperties中获取[而非直接从配置文件中获取]
         */
        DataSourceProperties bean = context.getBean(DataSourceProperties.class);
        String url = bean.getUrl();
        String password = bean.getPassword();
        // url:jdbc:mysql://localhost:3306/test_spring,password:Woshij8dan!
        System.out.println("url:" + url + ",password:" + password);
    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {
    }

    static class MyImportSelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    // 如果不进行任何配置，将默认注入Hikari DataSource Bean到spring容器中
                    DataSourceAutoConfiguration.class.getName(),
                    // 最终导入三个组件[SqlSessionFactory, SqlSessionTemplate, AutoConfiguredMapperScannerRegistrar]
                    MybatisAutoConfiguration.class.getName(),
                    // 基于DataSource的事务管理器 -> 提交回滚事物
                    DataSourceTransactionManagerAutoConfiguration.class.getName(),
                    // 提供声明式的事务管理 -> 事物切面TransactionInterceptor，事物切点TransactionAttributeSource[@Transaction]，事物通知BeanFactoryTransactionAttributeSourceAdvisor
                    TransactionAutoConfiguration.class.getName()
            };
        }
    }
}
