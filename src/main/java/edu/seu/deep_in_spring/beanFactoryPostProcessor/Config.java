package edu.seu.deep_in_spring.beanFactoryPostProcessor;

import com.alibaba.druid.pool.DruidDataSource;
import edu.seu.deep_in_spring.beanFactoryPostProcessor.component.Mapper1;
import edu.seu.deep_in_spring.beanFactoryPostProcessor.component.Mapper2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ComponentScan("edu.seu.deep_in_spring.beanFactoryPostProcessor.component")
public class Config {

    /**
     * a test method with no @Bean
     */
    public void test() {
    }

    @Bean
    public Bean1 bean1() {
        return new Bean1();
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }

    @Bean(initMethod =  "init")
    public DruidDataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://localhost:3306");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("Woshij8dan!");
        return druidDataSource;
    }

    /**
     * 注入一个Mapper Bean
     * @param sqlSessionFactory SqlSessionFactoryBean依赖注入
     */
/*    @Bean
    public MapperFactoryBean<Mapper1> mapper1(SqlSessionFactory sqlSessionFactory) {
        MapperFactoryBean<Mapper1> factoryBean = new MapperFactoryBean<>(Mapper1.class);
        // 设置SqlSessionFactory
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean;
    }

    @Bean
    public MapperFactoryBean<Mapper2> mapper2(SqlSessionFactory sqlSessionFactory) {
        MapperFactoryBean<Mapper2> factoryBean = new MapperFactoryBean<>(Mapper2.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean;
    }*/
}
