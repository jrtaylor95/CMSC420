package cmsc420.structure.PRQuadtree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/*
 * Based on the PRQuadtree implementation from the slides from Virginia Tech
 */
public class PRQuadtree {
	private PRQuadtreeNode root;
	private int xMax, yMax;

	public PRQuadtree(int xMax, int yMax) {
		this.xMax = xMax;
		this.yMax = yMax;

		root = PRQuadtreeWhite.getInstance();
	}

	public void add(Point2D g) {
		Rectangle2D region = new Rectangle2D.Float(0, 0, xMax, yMax);
		
		root = root.add(g, region);
	}

	public void remove(Point2D g) {
		root = root.remove(g);
	}

	public boolean find(Point2D g) {
		return root.find(g);
	}

	public void clear() {
		root = PRQuadtreeWhite.getInstance();
	}

	public boolean isInBounds(Point2D point) {
		double x = point.getX();
		double y = point.getY();
		
		return x >= 0 && x < xMax && y >= 0 && y < yMax;
	}

	public boolean isEmpty() {
		return root.getType() == PRQuadtreeNode.WHITE;
	}

//	public PriorityQueue<T> getCitiesInRange(int x, int y, int radius) {
//		Circle2D circle = new Circle2D.Float(x, y, radius);
//
//		PriorityQueue<T> inRange = new PriorityQueue<T>(new CityNameComparator());
//		getCitiesInRangeHelper(root, inRange, circle);
//
//		return inRange;
//	}
//
//	private void getCitiesInRangeHelper(PRQuadtreeNode sRoot, PriorityQueue<T> inRange, Circle2D circle) {
//		PRQuadtreeBlack leaf;
//		PRQuadtreeGray internal;
//
//		if (sRoot instanceof PRQuadtreeBlack) {
//			leaf = (PRQuadtreeBlack) sRoot;
//			if (Inclusive2DIntersectionVerifier.intersects(leaf.element, circle))
//				inRange.add(leaf.element);	
//		} else if (sRoot instanceof PRQuadtreeGray) {
//			internal = (PRQuadtreeGray) sRoot;
//			getCitiesInRangeHelper(internal.ne, inRange, circle);
//			getCitiesInRangeHelper(internal.nw, inRange, circle);
//			getCitiesInRangeHelper(internal.se, inRange, circle);
//			getCitiesInRangeHelper(internal.sw, inRange, circle);
//		}
//	}
//
//	public PriorityQueue<T> getCitiesInRange(int x, int y, int radius, String saveMap) throws IOException {
//		PriorityQueue<T> inRange = getCitiesInRange(x, y, radius);
//
//		if (!inRange.isEmpty()) {
//			CanvasPlus canvas = printToCanvas();
//			canvas.addCircle(x, y, radius, Color.blue, false);
//			canvas.save(saveMap);
//			canvas.dispose();
//		}
//		return inRange;
//	}
//
//	public T getNearest(int x, int y) {
//		PRQuadtreeNode ele;
//		PriorityQueue<PRQuadtreeNode> queue = new PriorityQueue<PRQuadtreeNode>(new CoordinateDistanceComparator<PRQuadtreeNode>(x, y));
//		queue.add(root);
//
//		while (!queue.isEmpty()) {
//			ele = queue.remove();
//			if (ele instanceof PRQuadtreeBlack)
//				return ((PRQuadtreeBlack) ele).element;
//			else if (ele instanceof PRQuadtreeGray) {
//				if (!(((PRQuadtreeGray) ele).ne instanceof PRQuadtreeWhite))
//					queue.add(((PRQuadtreeGray) ele).ne);
//				if (!(((PRQuadtreeGray) ele).nw instanceof PRQuadtreeWhite))
//					queue.add(((PRQuadtreeGray) ele).nw);
//				if (!(((PRQuadtreeGray) ele).se instanceof PRQuadtreeWhite))
//					queue.add(((PRQuadtreeGray) ele).se);
//				if (!(((PRQuadtreeGray) ele).sw instanceof PRQuadtreeWhite))
//					queue.add(((PRQuadtreeGray) ele).sw);
//			}
//		}
//
//		return null;
//	}
}
