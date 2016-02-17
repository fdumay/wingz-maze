package com.wingz.maze.controller;

import com.google.common.collect.Sets;
import com.wingz.maze.WingzMazeApp;
import com.wingz.maze.domain.PathItem;
import com.wingz.maze.domain.Point;
import com.wingz.maze.helper.PointBuilder;
import com.wingz.maze.service.MapService;
import com.wingz.maze.testutils.JsonTestUtils;
import com.wingz.maze.testutils.WingzMazeTestConfig;
import com.wingz.maze.testutils.WingzMazeTestUtils;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collection;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WingzMazeApp.class, WingzMazeTestConfig.class})
@WebIntegrationTest(value = WingzMazeTestUtils.TEST_INTEGRATION_ATTRIBUTE)
public class MazeSolverControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MapService mapService;

	@Before
	public void mockMap() {
		Collection<Point> points = Sets.newHashSet();
		points.add(PointBuilder.create("a").link("b", 2).link("c", 6).build());
		points.add(PointBuilder.create("b").link("e", 2).build());
		points.add(PointBuilder.create("c").link("b", 3).link("d", 2).build());
		points.add(PointBuilder.create("d").build());
		points.add(PointBuilder.create("e").link("c", 1).link("d", 7).build());
		mapService.addMap("map1.json", points);
	}

	@Test
	public void checkParam() throws Exception {
		mockMvc
			.perform(get(MazeSolverController.SOLVE_PATH))
			.andExpect(status().isBadRequest());
		mockMvc
			.perform(get(MazeSolverController.SOLVE_PATH)
				.param(MazeSolverController.PARAM_START, "a"))
			.andExpect(status().isBadRequest());
		mockMvc
			.perform(get(MazeSolverController.SOLVE_PATH)
				.param(MazeSolverController.PARAM_END, "b"))
			.andExpect(status().isBadRequest());
		mockMvc
			.perform(get(MazeSolverController.SOLVE_PATH)
				.param(MazeSolverController.PARAM_START, "a")
				.param(MazeSolverController.PARAM_END, "b"))
			.andExpect(status().isOk());

		mockMvc
			.perform(get(MazeSolverController.SOLVE_PATH)
				.param(MazeSolverController.PARAM_START, "a")
				.param(MazeSolverController.PARAM_END, "dummy"))
			.andExpect(status().isBadRequest());
	}

	/**
	 * a - b : 2
	 *
	 * @throws Exception
	 */
	@Test
	public void findAToB() throws Exception {
		testAndAssert("a", "b", 2, 2);
	}

	/**
	 * a - b - e : 4
	 *
	 * @throws Exception
	 */
	@Test
	public void findAToE() throws Exception {
		testAndAssert("a", "e", 3, 4);
	}

	/**
	 * a - b - e - c : 5
	 *
	 * @throws Exception
	 */
	@Test
	public void findAToC() throws Exception {
		testAndAssert("a", "c", 4, 5);
	}

	private void testAndAssert(String start, String end, int pathSize, int pathLength) throws Exception {
		MvcResult result = mockMvc
			.perform(get(MazeSolverController.SOLVE_PATH)
				.param(MazeSolverController.PARAM_START, start)
				.param(MazeSolverController.PARAM_END, end))
			.andExpect(status().isOk())
			.andReturn();

		List<PathItem> path = JsonTestUtils.asList(result, PathItem.class);
		assertEquals(pathSize, path.size());
		assertEquals(pathLength, path.get(path.size() - 1).getDistanceFromBegining());

	}

}
