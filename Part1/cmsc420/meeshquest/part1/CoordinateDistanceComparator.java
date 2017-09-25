package cmsc420.meeshquest.part1;

import java.util.Comparator;

public class CoordinateDistanceComparator<T extends PRQuadtreeNode> implements Comparator<T> {
	int xOffset, yOffset;

	public CoordinateDistanceComparator(int x, int y) {
		xOffset = x;
		yOffset = y;
	}

	@Override
	public int compare(T o1, T o2) {
		if (o1 instanceof PRQuadtreeGray && o2 instanceof PRQuadtreeBlack) {
			return -1;
		} else if (o1 instanceof PRQuadtreeBlack && o2 instanceof PRQuadtreeGray) {
			return 1;
		} else {
			double dist1 = Math.sqrt(Math.pow(o1.x - xOffset, 2) + Math.pow(o1.y - yOffset, 2));
			double dist2 = Math.sqrt(Math.pow(o2.x - xOffset, 2) + Math.pow(o2.y - yOffset, 2));

			if (dist1 > dist2)
				return 1;
			else if (dist1 < dist2)
				return -1;
			else {
				if (o1 instanceof PRQuadtreeBlack && o2 instanceof PRQuadtreeBlack) {
					String city1 = ((PRQuadtreeBlack<?>) o1).element.getName();
					String city2 = ((PRQuadtreeBlack<?>) o2).element.getName();
					if (city1.compareTo(city2) > 0)
						return -1;
					else
						return 1;
				}
			}
			return 0;
		}
	}

}
