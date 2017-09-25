package cmsc420.Structure;


import java.awt.geom.Point2D;
import java.util.Comparator;

import cmsc420.geom.Geometry2D;

public class CoordinateComparator implements Comparator<Geometry2D> {

	@Override
	public int compare(Geometry2D e1, Geometry2D e2) {
		Point2D point1, point2;
		
		if (e1.getType() == Geometry2D.POINT && e2.getType() == Geometry2D.POINT) {
			point1 = (Point2D) e1;
			point2 = (Point2D) e2;
			
			if (point1.getY() > point2.getY())
				return 1;
			else if (point1.getY() < point2.getY())
				return -1;
			else {
				if (point1.getX() > point2.getX())
					return 1;
				else if (point1.getX() == point2.getX())
					return 0;
				else
					return -1;
			}
		}
		return 0;
	}
}
