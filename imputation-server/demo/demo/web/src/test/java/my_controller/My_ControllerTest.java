package my_controller;

import static org.junit.Assert.*;

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

import demoApplication.DemoApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = DemoApplication.class)
public class My_ControllerTest {
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;
	
	@Before
	public void setMockMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	@Test
	public void test() throws Exception {
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~test!");
		mockMvc.perform(MockMvcRequestBuilders.get("/my")
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
