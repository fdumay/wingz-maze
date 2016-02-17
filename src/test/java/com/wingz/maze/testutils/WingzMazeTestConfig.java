package com.wingz.maze.testutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class WingzMazeTestConfig {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Bean
	public MockMvc mvcMock() {
		return MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
			.build();
	}

}
