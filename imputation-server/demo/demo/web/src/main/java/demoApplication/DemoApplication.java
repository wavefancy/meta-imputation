package demoApplication;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EntityScan(basePackages = {"my.entity", "bigfileupload.entity"})
@ComponentScan(basePackages={"demoApplication"
		, "datasource.config", "interceptor.config", "apache.shiro.config"
		, "my.dao", "my.exception", "my.handle", "my.service", "my.controller"
		, "bigfileupload.service"
		})
@EnableTransactionManagement
//@MapperScan(basePackages = "my_dao", sqlSessionTemplateRef = "mysqlSessionTemplate")
public class DemoApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(DemoApplication.class, args);
	}

}
