package cmsc420.structure.comparators;


import java.util.Comparator;

import cmsc420.structure.SpatialMapPoint;

public class CoordinateComparator implements Comparator<SpatialMapPoint> {

	private static final CoordinateComparator INSTANCE = new CoordinateComparator();
	
	public static CoordinateComparator getInstance() {
		return INSTANCE;
	}
	
	@Override
	public int compare(SpatialMapPoint e1, SpatialMapPoint e2) {

		if (e1.getRemoteY() > e2.getRemoteY())
			return 1;
		else if (e1.getRemoteY() < e2.getRemoteY())
			return -1;
		else {
			if (e1.getRemoteX() > e2.getRemoteX())
				return 1;
			else if (e1.getRemoteX() < e2.getRemoteX())
				return -1;
			else {
				if (e1.getY() > e2.getY())
					return 1;
				else if (e1.getY() < e2.getY())
					return -1;
				else {
					if (e1.getX() > e2.getX())
						return 1;
					else if (e1.getX() < e2.getX())
						return -1;
					else
						return 0;
				}
			}
		}
	}
}
