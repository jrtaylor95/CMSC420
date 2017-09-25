package cmsc420.structure;
import cmsc420.SpatialMapLine;
import cmsc420.geom.Geometry2D;

public class Road extends SpatialMapLine {
	double length;

	public Road(SpatialMapPoint start, SpatialMapPoint end) {
		if (start.getName().compareTo(end.getName()) < 0) {
			this.p1 = start;
			this.p2 = end;
		} else {
			this.p1 = end;
			this.p2 = start;
		}
		length = start.distance(end);
	}

	@Override
	public int getType() {
		return Geometry2D.SEGMENT;
	}
	
	public boolean equals(Geometry2D g2) {
		if (g2.getType() == Geometry2D.SEGMENT)
			return equals((Road) g2);
		
		return false;
	}
	
	public double getLength() {
		return length;
	}
}
