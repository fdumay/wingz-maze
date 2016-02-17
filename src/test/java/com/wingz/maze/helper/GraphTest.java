package com.wingz.maze.helper;

import com.wingz.maze.domain.PathItem;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class GraphTest {

	private Graph graph;

	@Before
	public void mockMap() {
		graph = new Graph();
		graph.addPoint(PointBuilder.create("a").link("b", 2).link("c", 6).build());
		graph.addPoint(PointBuilder.create("b").link("e", 2).build());
		graph.addPoint(PointBuilder.create("c").link("b", 3).link("d", 2).build());
		graph.addPoint(PointBuilder.create("d").build());
		graph.addPoint(PointBuilder.create("e").link("c", 1).link("d", 7).build());
	}

	/**
	 * a - b : 2
	 *
	 * @throws Exception
	 */
	@Test
	public void findAToB() throws Exception {
		List<PathItem> path = graph.computeShortestPath("a", "b");
		assertEquals(2, path.size());
		assertPathLenth(2, path);
	}

	/**
	 * a - b - e - c - d : 7
	 *
	 * @throws Exception
	 */
	@Test
	public void findAToE() throws Exception {
		List<PathItem> path = graph.computeShortestPath("a", "d");
		assertEquals(5, path.size());
		assertPathLenth(7, path);
	}

	/**
	 * a - b - e - c : 5
	 *
	 * @throws Exception
	 */
	@Test
	public void findAToC() throws Exception {
		List<PathItem> path = graph.computeShortestPath("a", "c");
		assertEquals(4, path.size());
		assertPathLenth(5, path);

	}

	private void assertPathLenth(int expected, List<PathItem> path) {
		PathItem last = path.get(path.size() - 1);
		assertEquals(expected, last.getDistanceFromBegining());
	}

	/**
	 * no solutions
	 *
	 * @throws Exception
	 */
	@Test
	public void findDToE() throws Exception {
		List<PathItem> path = graph.computeShortestPath("d", "e");
		assertEquals(0, path.size());
	}

}
