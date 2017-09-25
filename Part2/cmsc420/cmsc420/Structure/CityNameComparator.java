package cmsc420.Structure;


import java.util.Comparator;

import cmsc420.geom.Geometry2D;

public class CityNameComparator implements Comparator<Geometry2D> {
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
				int comp = ((Road) o2).start.getName().compareTo(((Road) o1).start.getName());
				if (comp == 0)
					return ((Road) o2).end.getName().compareTo(((Road) o1).end.getName());
				
				return comp;
			} else 
				return 0;
		}
	}
}
