package cmsc420;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import cmsc420.geom.Geometry2D;
import cmsc420.structure.SpatialMapPoint;

public class SpatialMapLine extends Line2D implements Geometry2D{

	protected SpatialMapPoint p1;
	protected SpatialMapPoint p2;
	
	public boolean equals(SpatialMapLine l) {
		return p1.equals(l.p1) && p2.equals(l.p2);
	}
	
	@Override
	public Rectangle2D getBounds2D() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {
		return Geometry2D.SEGMENT;
	}

	@Override
	public double getX1() {
		return p1.getX();
	}

	@Override
	public double getY1() {
		return p1.getY();
	}

	@Override
	public Point2D getP1() {
		return p1;
	}
	
	public SpatialMapPoint getSMP1() {
		return p1;
	}

	@Override
	public double getX2() {
		return p2.getX();
	}

	@Override
	public double getY2() {
		return p2.getY();
	}

	@Override
	public Point2D getP2() {
		return p2;
	}
	
	public SpatialMapPoint getSMP2() {
		return p2;
	}

	@Override
	public void setLine(double x1, double y1, double x2, double y2) {
		p1.setLocation(x1, y1);
		p2.setLocation(x2, y2);
	}

}
