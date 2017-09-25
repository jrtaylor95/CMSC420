package cmsc420.structure;

import java.util.TreeMap;

import cmsc420.geom.Geometry2D;
import cmsc420.structure.comparators.GeometryNameComparator;

public class City extends SpatialMapPoint {
	private String color;
	private int radius;
	boolean isolated;
	TreeMap<SpatialMapPoint, Road>connectingRoads;

	public City(String name, int localX, int localY, int remoteX, int remoteY, int radius, String color) {
		this.localX = localX;
		this.localY = localY;
		this.remoteX = remoteX;
		this.remoteY = remoteY;
		this.name = name;
		this.radius = radius;
		this.color = color;
		connectingRoads = new TreeMap<SpatialMapPoint, Road>(GeometryNameComparator.getInstance());
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public float getRadius() {
		return radius;
	}

	public boolean equals(City c) {
		return localY == c.localY && localX == c.localX && name.equals(c.name) && remoteX == c.getRemoteX() && remoteY == c.getRemoteY();
	}

	public String toString() {
		return "(" + localX + "," + localY + ")";
	}

	public void setIsolated(boolean isolated) {
		this.isolated = isolated;
	}

	public boolean isIsolated() {
		return isolated;
	}

	public boolean equals(Geometry2D g2) {
		if (g2.getType() == POINT)
			return equals((City) g2);

		return false;
	}

	@Override
	public int getStucture() {
		return CITY;
	}

	public TreeMap<SpatialMapPoint, Road> getConnectingRoads() {
		return connectingRoads;
	}

	public Road getConnectingRoad(SpatialMapPoint end) {
		return connectingRoads.get(end);
	}
	@Override
	public boolean equals(SpatialMapPoint point) {
		if (point.getStucture() != CITY)
			return false;

		return this.equals((City) point);
	}

	public void addConnectingRoad(Road road) {
		if (road.getSMP1().equals(this))
			connectingRoads.put(road.getSMP2(), road);
		else
			connectingRoads.put(road.getSMP1(), road);
	}

	public Road removeConnectingRoad(SpatialMapPoint end) {
		return connectingRoads.remove(end);
	}
}
