package com.wingz.maze.helper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wingz.maze.domain.Link;
import com.wingz.maze.domain.PathItem;
import com.wingz.maze.domain.Point;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Graph {

	private final Map<String, Point> map = Maps.newLinkedHashMap();

	public void addPoint(Point point) {
		this.map.put(point.getId(), point);
	}

	public List<PathItem> computeShortestPath(String start, String end) {
		PriorityQueue<PathItem> nodes = new PriorityQueue<PathItem>();
		Map<String, Integer> distances = Maps.newHashMap();
		Map<String, PathItem> previous = Maps.newHashMap();

		for (String pointId : map.keySet()) {
			if (pointId.equals(start)) {
				distances.put(pointId, 0);
				nodes.add(new PathItem(pointId, 0, 0));
			} else {
				distances.put(pointId, Integer.MAX_VALUE);
				nodes.add(new PathItem(pointId, Integer.MAX_VALUE, Integer.MAX_VALUE));
			}
		}

		while (!nodes.isEmpty()) {
			PathItem point = nodes.poll();
			String pointId = point.getId();

			for (Link link : map.get(pointId).getLinks()) {
				String child = link.getId();
				int linkDist = link.getDistance();

				int distFromPoint = distances.get(pointId).intValue();
				int distToChild = distances.get(child).intValue();
				if ((distFromPoint + linkDist) < distToChild) {
					int newDist = distFromPoint == Integer.MAX_VALUE ? linkDist : distFromPoint + linkDist;
					distances.put(child, newDist);
					previous.put(child, new PathItem(pointId, linkDist, newDist));

					PathItem nodeToSort = null;
					for (PathItem pathElem : nodes) {
						if (pathElem.getId().equals(child)) {
							// pathElem.setDistance(link.getDistance());
							pathElem.setDistanceFromBegining(newDist);
							nodeToSort = pathElem;
							break;
						}
					}
					if (nodeToSort != null) {
						nodes.remove(nodeToSort);
						nodes.add(nodeToSort);
					}
				}
			}
		}

		List<PathItem> path = Lists.newArrayList();
		String currentId = end;
		while (currentId != null) {
			PathItem previousLink = previous.get(currentId);
			if (previousLink == null) {
				return Collections.EMPTY_LIST;
			}
			path.add(new PathItem(currentId, previousLink.getDistance(), previousLink.getDistanceFromBegining()));

			currentId = previousLink.getId();
			if (currentId.equals(start)) {
				path.add(new PathItem(start, 0, 0));
				Collections.reverse(path);
				return path;
			}
		}
		return Collections.EMPTY_LIST;
	}

}