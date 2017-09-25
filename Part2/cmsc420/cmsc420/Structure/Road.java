package cmsc420.Structure;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;

import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;

public class Road extends Line2D implements Geometry2D {
	public City start, end;
	int x1, x2, y1, y2;

	public Road(City start, City end) {

		if (start.getName().compareTo(end.getName()) < 0) {
			this.start = start;
			this.end = end;
		} else {
			this.start = end;
			this.end = start;
		}
	}

	public void toXml(Element parent) {
		Element roadElement = parent.getOwnerDocument().createElement("road");
		roadElement.setAttribute("start", start.getName());
		roadElement.setAttribute("end", end.getName());

		parent.appendChild(roadElement);
	}

	@Override
	public int getType() {
		return Geometry2D.SEGMENT;
	}

	@Override
	public Rectangle2D getBounds2D() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getX1() {
		return start.getX();
	}

	@Override
	public double getY1() {
		return start.getY();
	}

	@Override
	public Point2D getP1() {
		return start;
	}

	@Override
	public double getX2() {
		return end.getX();
	}

	@Override
	public double getY2() {
		return end.getY();
	}

	@Override
	public Point2D getP2() {
		return end;
	}
	
	public boolean equals(Road r2) {
		return start.equals(r2.start) && end.equals(r2.end);
	}
	
	public boolean equals(Geometry2D g2) {
		if (g2.getType() == Geometry2D.SEGMENT)
			return equals((Road) g2);
		
		return false;
	}

	@Override
	public void setLine(double x1, double y1, double x2, double y2) {
		this.x1 = (int) x1;
		this.y1 = (int) y1;
		this.x2 = (int) x2;
		this.y2 = (int) y2;
	}
	
	public boolean intersectsExcludingEndpoints(Road r2) {
		if (Inclusive2DIntersectionVerifier.intersects((Line2D) this, (Line2D) r2)) {
			//If the endpoints of this road does not equal the endpoints of the other road then it intersects
			if (!start.equals(r2.start) && !start.equals(r2.end) && !end.equals(r2.start) && !end.equals(r2.end))
				return true;
		}
		
		return false;
	}
}
