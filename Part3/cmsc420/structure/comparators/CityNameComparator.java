package cmsc420.structure.comparators;


import java.util.Comparator;

import cmsc420.geom.Geometry2D;
import cmsc420.structure.City;
import cmsc420.structure.Road;

public class CityNameComparator implements Comparator<Geometry2D> {
	private static CityNameComparator INSTANCE = new CityNameComparator();
	
	public static CityNameComparator getInstance() {
		return INSTANCE;
	}
	
	@Override
	public int compare(Geometry2D o1, Geometry2D o2) {
		if (o1.getType() == Geometry2D.POINT && o2.getType() == Geometry2D.SEGMENT)
			return -1;
		else if (o1.getType() == Geometry2D.SEGMENT && o2.getType() == Geometry2D.POINT)
			return 1;
		else {
			if (o1.getType() == Geometry2D.POINT)
				return ((City) o2).getName().compareTo(((City) o1).getName());
			else if (o1.getType() == Geometry2D.SEGMENT) {
				int comp = ((Road) o2).getSMP1().getName().compareTo(((Road) o1).getSMP1().getName());
				if (comp == 0)
					return ((Road) o2).getSMP2().getName().compareTo(((Road) o1).getSMP2().getName());
				
				return comp;
			} else 
				return 0;
		}
	}
}
