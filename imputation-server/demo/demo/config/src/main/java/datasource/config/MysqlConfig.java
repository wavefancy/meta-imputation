package datasource.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 配置datasource到ioc容器里面
 *
 * @author xiangze
 *
 */
@Configuration
// 配置mybatis mapper的扫描路径
@MapperScan(basePackages = "my.dao", sqlSessionTemplateRef = "mysqlSessionTemplate")
public class MysqlConfig {

	@Value("${mybatis.configLocation}")
	private String mybatisConfigFile;

	@Value("${mybatis.mapper-locations}")
	private String mapperPath;

	@Value("${mybatis.type-aliases-package}")
	private String typeAliasPackage;

	/**
	 * 创建数据源
	 * 
	 * @return DataSource
	 */
	@Bean(name = "mysqlDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.mysql")
	public DataSource mysqlDataSource() {
		return DataSourceBuilder.create().build();
	}

	/**
	 * 创建工厂
	 * 
	 * @param dataSource
	 * @throws Exception
	 * @return SqlSessionFactory
	 */
	@Bean(name = "mysqlSessionFactory")
	public SqlSessionFactory mysqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setConfigLocation(new ClassPathResource(mybatisConfigFile));
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperPath));
		bean.setTypeAliasesPackage(typeAliasPackage);
		return bean.getObject();
	}

	/**
	 * 创建事务
	 * 
	 * @param dataSource
	 * @return DataSourceTransactionManager
	 */
	@Bean(name = "mysqlTransactionManager")
	public DataSourceTransactionManager mysqlDataSourceTransactionManager(
			@Qualifier("mysqlDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	/**
	 * 创建模板
	 * 
	 * @param sqlSessionFactory
	 * @return SqlSessionTemplate
	 */
	@Bean(name = "mysqlSessionTemplate")
	public SqlSessionTemplate mysqlSessionTemplate(
			@Qualifier("mysqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
