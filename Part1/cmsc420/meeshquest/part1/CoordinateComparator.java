package cmsc420.meeshquest.part1;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class CoordinateComparator<T extends Point2D.Float> implements Comparator<T> {
	
	@Override
	public int compare(T e1, T e2) {
		if (e1.y > e2.y)
			return 1;
		else if (e1.y < e2.y)
			return -1;
		else {
			if (e1.x > e2.x)
				return 1;
			else if (e1.x == e2.x)
				return 0;
			else
				return -1;
		}
	}
}
