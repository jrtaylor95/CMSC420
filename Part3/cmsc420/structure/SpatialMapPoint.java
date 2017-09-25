package cmsc420.structure;

import java.awt.geom.Point2D;

import cmsc420.geom.Geometry2D;

public abstract class SpatialMapPoint extends Point2D implements Geometry2D {

	public final static int CITY = 4;
	public final static int AIRPORT = 5;
	public final static int TERMINAL = 6;
	
	String name;
	int localX, localY, remoteX, remoteY;
	
	@Override
	public int getType() {
		return POINT;
	}
	
	public double getRemoteX() {
		return remoteX;
	}
	
	public double getRemoteY() {
		return remoteY;
	}

	@Override
	public double getX() {
		return localX;
	}

	@Override
	public double getY() {
		return localY;
	}

	@Override
	public void setLocation(double x, double y) {
		localX = (int) x;
		localY = (int) y;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract String toString();
	
	public abstract boolean equals(SpatialMapPoint point);
	
	public abstract int getStucture();
}
