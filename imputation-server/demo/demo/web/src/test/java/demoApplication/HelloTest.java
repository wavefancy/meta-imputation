package demoApplication;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class HelloTest {
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;
	
	@Before
	public void setMockMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	/*
	@Test
	public void test() throws Exception {
		String responseString = mockMvc.perform(get("/categories/getAllCategory") //请求的url,请求的方法是get
				.contentType(MediaType.APPLICATION_FORM_URLENCODED) //数据的格式
				.param("pcode","root"))                             //添加参数
				.andExpect(status().isOk())                         //返回的状态是200
				.andDo(print())                                     //打印出请求和相应的内容
				.andReturn()
				.getResponse()
				.getContentAsString();                              //将相应的数据转换为字符串
		System.out.println("--------返回的json = " + responseString);
	}
	
	@Test
	public void testCreateSeewoAccountUser() throws Exception {
		mockMvc.perform(post("/sjzyglyfw/users")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
		.andExpect(status().isOk())
		.andExpect((ResultMatcher) jsonPath("$.data.name", is("测试")))
		.andExpect((ResultMatcher) jsonPath("$.data.createTime", notNullValue()));
    }
	*/
	
	@Test
	public void test() throws Exception {
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~test!");
		mockMvc.perform(MockMvcRequestBuilders.get("/hello")
				.accept(MediaType.ALL)
		//		.contentType(MediaType.APPLICATION_JSON)
		//		.content(jsonObject.toJSONString())
				)
		.andExpect(MockMvcResultMatchers.status().isOk())   //返回的状态是200
		.andDo(MockMvcResultHandlers.print())               //打印出请求和相应的内容        
		/*                               
		.andReturn()
		.getResponse()
		.getContentAsString();                              //将相应的数据转换为字符串
		*/
		;
	}
	

}
