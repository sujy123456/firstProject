package com.example.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.example.mapper")
public class MyBatisConfig {

    // 手动强制注入 SqlSessionFactory，解决版本兼容问题
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        
        // 设置Mapper XML文件位置
        Resource[] mapperLocations = new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/*.xml");
        factory.setMapperLocations(mapperLocations);
        
        // 设置类型别名包
        factory.setTypeAliasesPackage("com.example.entity");
        
        // 设置MyBatis配置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(configuration);
        
        return factory.getObject();
    }
}
