package com.wingz.maze.domain;

import java.io.Serializable;

public class Link implements Serializable {

	private String id;
	private int distance;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

}
