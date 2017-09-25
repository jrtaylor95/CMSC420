package cmsc420.structure.comparators;

import java.util.Comparator;

import cmsc420.geom.Geometry2D;
import cmsc420.structure.City;
import cmsc420.structure.Road;

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
			if (road.getSMP1().equals((City) o1) || road.getSMP2().equals((City) o1))
				return 1;
			else if (road.getSMP1().equals((City) o2) || road.getSMP2().equals((City) o2))
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
