package cmsc420.Structure;

import java.awt.geom.Point2D;
import java.util.Comparator;

import cmsc420.geom.Geometry2D;

public class RoadCityDistanceComparator implements Comparator<Geometry2D> {
	Point2D point;

	public RoadCityDistanceComparator(int x, int y) {
		point = new Point2D.Float(x, y);
	}

	@Override
	public int compare(Geometry2D o1, Geometry2D o2) {
		Road road1, road2;
		double distance1, distance2;

		if (o1.getType() == Geometry2D.POINT && o2.getType() != Geometry2D.POINT)
			return 1;
		else if (o1.getType() != Geometry2D.POINT && o2.getType() == Geometry2D.POINT)
			return -1;
		else if (o1.getType() == Geometry2D.POINT && o2.getType() == Geometry2D.POINT)
			return 1;
		else {
			road1 = (Road) o1;
			road2 = (Road) o2;

			distance1 = road1.ptSegDist(point);
			distance2 = road2.ptSegDist(point);

			if (distance1 < distance2)
				return -1;
			else if (distance1 > distance2)
				return 1;
			else {
				int comp = road2.start.getName().compareTo(road1.start.getName());

				if (comp == 0)
					return road2.end.getName().compareTo(road1.end.getName());

				return comp;
			}	
		}	
	}

}
