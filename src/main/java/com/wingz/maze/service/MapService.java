package com.wingz.maze.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wingz.maze.client.WingzClient;
import com.wingz.maze.domain.Point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MapService {

	@Autowired
	private WingzClient wingzClient;

	private Map<String, List<Point>> mapsCache;

	private void ensureCache() {
		if (mapsCache == null) {
			mapsCache = Maps.newHashMap();
			List<String> mapIds = wingzClient.listMaps();
			mapIds.forEach(mapId -> {
				List<Point> points = wingzClient.getMap(mapId);
				mapsCache.put(mapId, points);
			});
		}
	}

	public List<String> listMaps() {
		ensureCache();
		return Lists.newArrayList(mapsCache.keySet());
	}

	public List<Point> getMap(String mapId) {
		ensureCache();
		String mapKey = mapId;
		if (!mapKey.endsWith(".json")) {
			mapKey += ".json";
		}
		List<Point> points = mapsCache.get(mapKey);
		if (points == null) {
			points = Collections.EMPTY_LIST;
		}
		return points;
	}

}
