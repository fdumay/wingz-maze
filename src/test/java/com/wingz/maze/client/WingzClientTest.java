package com.wingz.maze.client;

import com.wingz.maze.WingzMazeApp;
import com.wingz.maze.domain.Point;
import com.wingz.maze.testutils.WingzMazeTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import feign.FeignException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WingzMazeApp.class)
@WebIntegrationTest(value = WingzMazeTestUtils.TEST_INTEGRATION_ATTRIBUTE)
public class WingzClientTest {

	@Autowired
	private WingzClient wingzClient;

	@Test
	public void listMap() {
		List<String> mapIds = wingzClient.listMaps();
		assertNotNull(mapIds);
		assertEquals(1, mapIds.size());
		assertEquals("map1.json", mapIds.get(0));
	}

	@Test
	public void getMap1Json() {
		List<Point> points = wingzClient.getMap("map1.json");
		assertNotNull(points);
		assertTrue(!points.isEmpty());
	}

	@Test
	public void fail404() {
		try {
			wingzClient.getMap("dummy");
			fail("expect 404");
		} catch (FeignException e) {
			// expected
		}
	}

}
