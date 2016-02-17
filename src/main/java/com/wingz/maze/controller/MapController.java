package com.wingz.maze.controller;

import com.wingz.maze.domain.Point;
import com.wingz.maze.service.MapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class MapController {

	public static final String MAPS_PATH = "/map";
	public static final String MAP_PATH = "/map/{mapId}";

	@Autowired
	private MapService mapService;

	@RequestMapping(method = RequestMethod.GET, value = MAP_PATH)
	public ResponseEntity<Collection<Point>> map(
		@PathVariable("mapId") String mapId) {

		Collection<Point> points = mapService.getMap(mapId);
		if (points.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(points, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = MAPS_PATH)
	public ResponseEntity<Collection<String>> map() {
		Collection<String> maps = mapService.listMaps();
		if (maps.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(maps, HttpStatus.OK);

	}
}
