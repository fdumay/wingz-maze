package com.wingz.maze.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

public class PathItem implements Comparable<PathItem> {

	private String id;
	private int distance;
	private int distanceFromBegining;

	public PathItem() {
	}

	public PathItem(String id, int distance, int distanceFromBegining) {
		super();
		this.id = id;
		this.distance = distance;
		this.distanceFromBegining = distanceFromBegining;
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

	public int getDistanceFromBegining() {
		return distanceFromBegining;
	}

	public void setDistanceFromBegining(int distanceFromBegining) {
		this.distanceFromBegining = distanceFromBegining;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PathItem) {
			PathItem other = (PathItem) obj;
			return Objects.equal(id, other.id);
		}
		return false;
	}

	@Override
	public int compareTo(PathItem o) {
		return ComparisonChain.start().compare(distanceFromBegining, o.distanceFromBegining).result();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return MoreObjects.toStringHelper(this).add("id", id).add("fromBegining", distanceFromBegining).add("distance",
			distance)
			.toString();
	}
}
