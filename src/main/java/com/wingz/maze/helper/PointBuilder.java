package com.wingz.maze.helper;

import com.wingz.maze.domain.Link;
import com.wingz.maze.domain.Point;

public class PointBuilder {

	private final Point point;

	private PointBuilder(String id) {
		point = new Point(id, id);
	}

	public static PointBuilder create(String id) {
		return new PointBuilder(id);
	}

	public PointBuilder link(String targetId, int distance) {
		point.getLinks().add(new Link(targetId, distance));
		return this;
	}

	public Point build() {
		return point;
	}
}
