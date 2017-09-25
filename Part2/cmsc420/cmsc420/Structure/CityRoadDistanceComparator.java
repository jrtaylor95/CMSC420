package cmsc420.Structure;

import java.util.Comparator;

import cmsc420.geom.Geometry2D;

public class CityRoadDistanceComparator implements Comparator<Geometry2D> {

	Road road;
	
	public CityRoadDistanceComparator(Road road) {
		this.road = road;
	}
	
	@Override
	public int compare(Geometry2D o1, Geometry2D o2) {
		if (o1.getType() == Geometry2D.POINT && o2.getType() != Geometry2D.POINT)
			return -1;
		else if (o1.getType() != Geometry2D.POINT && o2.getType() == Geometry2D.POINT)
			return 1;
		else if (o1.getType() != Geometry2D.POINT && o2.getType() != Geometry2D.POINT)
			return 1;
		else {
			if (road.start.equals((City) o1) || road.end.equals((City) o1))
				return 1;
			else if (road.start.equals((City) o2) || road.end.equals((City) o2))
				return -1;
			
			double dist1 = road.ptSegDist((City) o1);
			double dist2 = road.ptSegDist((City) o2);
			
			if (dist1 < dist2)
				return -1;
			else if (dist1 > dist2)
				return 1;
			else
				return ((City) o2).getName().compareTo(((City) o1).getName());
		}
	}
	
}
