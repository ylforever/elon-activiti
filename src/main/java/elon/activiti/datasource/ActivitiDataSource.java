package elon.activiti.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Activiti数据源配置
 */
@Configuration
@MapperScan(basePackages = "", value = "activitiSessionFactory")
public class ActivitiDataSource {

    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.activiti")
    public DataSource getDataSource(){
        DataSourceBuilder builder = DataSourceBuilder.create();
        return builder.build();
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource){
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        try {
            return sqlSessionFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
