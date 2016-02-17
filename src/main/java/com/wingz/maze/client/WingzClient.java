package com.wingz.maze.client;

import com.wingz.maze.domain.Point;

import java.util.List;

import feign.Param;
import feign.RequestLine;

public interface WingzClient {

	@RequestLine("GET /map")
	List<String> listMaps();

	@RequestLine("GET /map/{mapId}")
	List<Point> getMap(@Param("mapId") String mapId);
}
