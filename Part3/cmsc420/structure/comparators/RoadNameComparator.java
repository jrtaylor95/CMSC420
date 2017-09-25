package cmsc420.structure.comparators;

import java.util.Comparator;

import cmsc420.SpatialMapLine;
import cmsc420.structure.Road;

public class RoadNameComparator implements Comparator<SpatialMapLine> {
	private static final RoadNameComparator INSTANCE = new RoadNameComparator();

	public static RoadNameComparator getInstance() {
		return INSTANCE;
	}

	@Override
	public int compare(SpatialMapLine o1, SpatialMapLine o2) {
		int comp = ((Road) o2).getSMP1().getName().compareTo(((Road) o1).getSMP1().getName());
		if (comp == 0)
			return ((Road) o2).getSMP2().getName().compareTo(((Road) o1).getSMP2().getName());

		return comp;
	}
}

