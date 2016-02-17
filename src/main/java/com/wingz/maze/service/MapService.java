package com.wingz.maze.service;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wingz.maze.client.WingzClient;
import com.wingz.maze.domain.Point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class MapService {

	@Autowired
	private WingzClient wingzClient;

	private Map<String, Map<String, Point>> mapsCache;

	public void addMap(String mapId, Collection<Point> points) {
		if (mapsCache == null) {
			mapsCache = Maps.newHashMap();
		}
		Map<String, Point> map = Maps.newHashMap();
		for (Point point : points) {
			map.put(point.getId(), point);
		}
		mapsCache.put(mapId, map);
	}

	private void ensureCacheFromWingzApi() {
		if (mapsCache == null) {
			List<String> mapIds = wingzClient.listMaps();
			mapIds.forEach(mapId -> {
				List<Point> points = wingzClient.getMap(mapId);
				addMap(mapId, points);
			});
		}
	}

	public Collection<String> listMaps() {
		ensureCacheFromWingzApi();
		return Sets.newHashSet(mapsCache.keySet());
	}

	public Collection<Point> getMap(String mapId) {
		ensureCacheFromWingzApi();
		String mapKey = fixMapId(mapId);
		Map<String, Point> map = mapsCache.get(mapKey);
		if (map == null) {
			return Collections.EMPTY_LIST;
		}

		Collection<Point> points = map.values();
		if (points == null) {
			return Collections.EMPTY_LIST;
		}
		return points;
	}

	public Point getPoint(String mapId, String id) {
		ensureCacheFromWingzApi();
		String mapKey = fixMapId(mapId);
		Map<String, Point> map = mapsCache.get(mapKey);
		if (map == null) {
			return null;
		}
		return map.get(id);
	}

	private String fixMapId(String mapId) {
		String mapKey = mapId;
		if (!mapKey.endsWith(".json")) {
			mapKey += ".json";
		}
		return mapKey;
	}

}
