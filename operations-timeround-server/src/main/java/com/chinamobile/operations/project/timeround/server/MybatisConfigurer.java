package com.chinamobile.operations.project.timeround.server;

import com.chinamobile.operations.project.seed.mybatis.configurer.AbstractMybatisConfigurer;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author cpeng
 */
@Configuration
public class MybatisConfigurer extends AbstractMybatisConfigurer {
    /**
     * 项目基础包名称，根据公司实际项目修改
     */
    public static final String BASE_PACKAGE = "com.chinamobile.operations.project.timeround.server";
    public static final String CORE_PACKAGE = "com.chinamobile.operations.project.seed.log";

    @Override
    public String getTypeAliasesPackage() {
        return BASE_PACKAGE + ".model"
                + "," + CORE_PACKAGE + ".model";
    }

    @Override
    public String getBasePackage() {
        return BASE_PACKAGE + ".dao" 	+ "," + CORE_PACKAGE + ".dao";
    }

    @Override
    public String getMapperLocations() {
        return "classpath*:mybatis/**/*.xml";
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception{
        return getSqlSessionFactory(dataSource);
    }
}
