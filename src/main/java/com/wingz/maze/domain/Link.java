package com.wingz.maze.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public class Link implements Serializable {

	private String id;
	private int distance;

	public Link() {
	}

	public Link(String id, int distance) {
		super();
		this.id = id;
		this.distance = distance;
	}

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

	@Override
	public int hashCode() {
		return Objects.hashCode(id, distance);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Link) {
			Link other = (Link) obj;
			return Objects.equal(id, other.id)
				&& Objects.equal(distance, other.distance);
		}
		return false;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("distance", distance).toString();
	}
}
