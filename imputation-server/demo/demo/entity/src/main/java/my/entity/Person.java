package my.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 创建了一个实体类。
 *
 * 如何持久化呢？
 *
 * 1、使用@Entity进行实体类的持久化操作，当JPA检测到我们的实体类当中有
 *
 * @Entity 注解的时候，会在数据库中生成对应的表结构信息。
 *
 *
 * 如何指定主键以及主键的生成策略？
 *
 * 2、使用@Id指定主键.
 *
 *
 *
 * @author 赵雨虹
 * @version v.0.1
 * @date 2019年6月2日
 */
@Entity
public class Person {
	/**
	 * PS：@GeneratedValue注解的strategy属性提供四种值：
		–AUTO： 主键由程序控制，是默认选项，不设置即此项。
		–IDENTITY：主键由数据库自动生成，即采用数据库ID自增长的方式，Oracle不支持这种方式。
		–SEQUENCE：通过数据库的序列产生主键，通过@SequenceGenerator 注解指定序列名，mysql不支持这种方式。
		–TABLE：通过特定的数据库表产生主键，使用该策略可以使应用更易于数据库移植。
	 */
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
	
	@Min(value = 18, message = "未成年人禁止入内")
	@NotNull
    private Integer Age;
	
	@NotBlank(message = "这个字段必传")
    private String Name;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getAge() {
		return Age;
	}

	public void setAge(Integer age) {
		Age = age;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
}
