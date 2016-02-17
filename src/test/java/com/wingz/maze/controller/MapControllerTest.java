package com.wingz.maze.controller;

import com.wingz.maze.WingzMazeApp;
import com.wingz.maze.domain.Point;
import com.wingz.maze.testutils.JsonTestUtils;
import com.wingz.maze.testutils.WingzMazeTestConfig;
import com.wingz.maze.testutils.WingzMazeTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WingzMazeApp.class, WingzMazeTestConfig.class})
@WebIntegrationTest(value = WingzMazeTestUtils.TEST_INTEGRATION_ATTRIBUTE)
public class MapControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void listMap() throws Exception {
		MvcResult result = mockMvc
			.perform(get(MapController.MAPS_PATH))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();

		assertNotNull(result);
		List<String> maps = JsonTestUtils.asList(result, String.class);
		assertNotNull(maps);
		assertEquals(1, maps.size());
	}

	@Test
	public void getMap1Json() throws Exception {
		MvcResult result = mockMvc
			.perform(get(MapController.MAP_PATH, "map1.json"))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();

		assertNotNull(result);
		List<Point> points = JsonTestUtils.asList(result, Point.class);
		assertNotNull(points);
		assertFalse(points.isEmpty());
	}

	@Test
	public void getMap1() throws Exception {
		MvcResult result = mockMvc
			.perform(get(MapController.MAP_PATH, "map1"))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();

		assertNotNull(result);
		List<Point> points = JsonTestUtils.asList(result, Point.class);
		assertNotNull(points);
		assertFalse(points.isEmpty());
	}

	@Test
	public void mapNotFound() throws Exception {
		mockMvc
			.perform(get(MapController.MAP_PATH, "dummy"))
			.andExpect(status().isNoContent());
	}

}
