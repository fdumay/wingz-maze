package com.wingz.maze.controller;

import static com.google.common.base.Preconditions.checkState;

import com.wingz.maze.domain.PathItem;
import com.wingz.maze.domain.Point;
import com.wingz.maze.helper.Graph;
import com.wingz.maze.service.MapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
public class MazeSolverController {

	public static final String SOLVE_PATH = "/solve";

	public static final String PARAM_START = "start";
	public static final String PARAM_END = "end";
	public static final String PARAM_MAP = "mapId";

	@Autowired
	private MapService mapService;

	@RequestMapping(method = RequestMethod.GET, value = SOLVE_PATH)
	public ResponseEntity solveMaze(
		@RequestParam(value = PARAM_START, required = true) String start,
		@RequestParam(value = PARAM_END, required = true) String end,
		@RequestParam(value = PARAM_MAP, defaultValue = "map1.json") String mapId) {

		Point startPoint = mapService.getPoint(mapId, start);
		Point endPoint = mapService.getPoint(mapId, end);
		try {
			checkState(startPoint != null, "Start point not found");
			checkState(endPoint != null, "End point not found");
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

		Collection<Point> map = mapService.getMap(mapId);
		Graph graph = new Graph();
		map.forEach(point -> graph.addPoint(point));

		List<PathItem> path = graph.computeShortestPath(start, end);
		if (path.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(path, HttpStatus.OK);
	}

}
