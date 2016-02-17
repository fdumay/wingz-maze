package com.wingz.maze.service;

import com.wingz.maze.WingzMazeApp;
import com.wingz.maze.domain.Point;
import com.wingz.maze.testutils.WingzMazeTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WingzMazeApp.class)
@WebIntegrationTest(value = WingzMazeTestUtils.TEST_INTEGRATION_ATTRIBUTE)
public class MapServiceTest {

	@Autowired
	private MapService mapService;

	@Test
	public void listMap() {
		Collection<String> mapIds = mapService.listMaps();
		assertNotNull(mapIds);
		assertEquals(1, mapIds.size());
		assertEquals("map1.json", mapIds.iterator().next());
	}

	@Test
	public void getMap1Json() {
		Collection<Point> points = mapService.getMap("map1.json");
		assertNotNull(points);
		assertTrue(!points.isEmpty());
	}

	@Test
	public void getMap1() {
		Collection<Point> points = mapService.getMap("map1");
		assertNotNull(points);
		assertTrue(!points.isEmpty());
	}

	@Test
	public void mapNotFound() {
		Collection<Point> points = mapService.getMap("dummy");
		assertNotNull(points);
		assertTrue(points.isEmpty());
	}

}
