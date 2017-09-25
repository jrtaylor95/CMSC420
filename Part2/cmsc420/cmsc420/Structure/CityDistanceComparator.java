package cmsc420.Structure;

import java.awt.geom.Point2D;
import java.util.Comparator;

import cmsc420.geom.Geometry2D;

public class CityDistanceComparator implements Comparator<Geometry2D> {
	Point2D point;

	public CityDistanceComparator(int x, int y) {
		point = new Point2D.Float(x, y);
	}

	@Override
	public int compare(Geometry2D o1, Geometry2D o2) {
		City city1, city2;
		double distance1, distance2;

		if (o1.getType() == Geometry2D.POINT && o2.getType() != Geometry2D.POINT)
			return -1;
		else if (o1.getType() != Geometry2D.POINT && o2.getType() == Geometry2D.POINT)
			return 1;
		else if (o1.getType() != Geometry2D.POINT && o2.getType() != Geometry2D.POINT)
			return 1;
		else {
			city1 = (City) o1;
			city2 = (City) o2;

			distance1 = city1.distance(point);
			distance2 = city2.distance(point);

			if (!city1.isolated && city2.isolated)
				return -1;
			else if (city1.isolated && !city2.isolated)
				return 1;
			else {
				if (distance1 < distance2)
					return -1;
				else if (distance1 > distance2)
					return 1;
				else
					return city2.getName().compareTo(city1.getName());
			}
		}		
	}

}
