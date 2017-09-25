package cmsc420.structure.comparators;

import java.util.Comparator;

import cmsc420.geom.Geometry2D;
import cmsc420.structure.Road;
import cmsc420.structure.SpatialMapPoint;

public class GeometryNameComparator implements Comparator<Geometry2D> {

	private final static GeometryNameComparator INSTANCE = new GeometryNameComparator();
	
	public static GeometryNameComparator getInstance() {
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
				return ((SpatialMapPoint) o2).getName().compareTo(((SpatialMapPoint) o1).getName());
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
