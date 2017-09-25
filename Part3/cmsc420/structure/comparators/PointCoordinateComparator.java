package cmsc420.structure.comparators;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class PointCoordinateComparator implements Comparator<Point2D> {
	private static final PointCoordinateComparator INSTANCE = new PointCoordinateComparator();
	
	@Override
	public int compare(Point2D o1, Point2D o2) {
		if (o1.getY() < o2.getY())
			return -1;
		else if (o1.getY() > o2.getY())
			return 1;
		else {
			if (o1.getX() < o2.getX())
				return -1;
			else if (o1.getX() > o2.getX())
				return 1;
			else
				return 0;
		}
	}
	
	public static PointCoordinateComparator getInstance() {
		return INSTANCE;
	}

}
