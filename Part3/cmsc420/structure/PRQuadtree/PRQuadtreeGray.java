package cmsc420.structure.PRQuadtree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.PriorityQueue;

import org.w3c.dom.Element;

import cmsc420.geom.Circle2D;

public class PRQuadtreeGray implements PRQuadtreeNode {
	final static int NUM_REGIONS = 4;

	Rectangle2D[] regions;
	PRQuadtreeNode[] children;

	protected PRQuadtreeGray(Rectangle2D region) {
		double halfHeight = region.getHeight() * .5;
		double halfWidth = region.getWidth() * .5;
		double minX = region.getMinX();
		double minY = region.getMinY();
		double centerX = region.getCenterX();
		double centerY = region.getCenterY();
		children = new PRQuadtreeNode[NUM_REGIONS];
		regions = new Rectangle2D[NUM_REGIONS];

		//NW
		regions[0] = new Rectangle2D.Double(minX, centerY, halfWidth, halfHeight);
		//NE
		regions[1] = new Rectangle2D.Double(centerX, centerY, halfWidth, halfHeight);
		//SW
		regions[2] = new Rectangle2D.Double(minX, minY, halfWidth, halfHeight);
		//SE
		regions[3] = new Rectangle2D.Double(centerX, minY, halfWidth, halfHeight);
		for (int i = 0; i < NUM_REGIONS; i++)
			children[i] = PRQuadtreeWhite.getInstance();
	}

	public int countWhiteNodes() {
		int count = 0;

		for (int i = 0; i < NUM_REGIONS; i++) {
			if (children[i].getType() == WHITE)
				count++;
		}

		return count;
	}

	@Override
	public PRQuadtreeNode add(Point2D g, Rectangle2D region) {
		PRQuadtreeNode node = getIntersectingRegion(g);
		
		node = node.add(g, region);
		return this;
	}

	@Override
	public PRQuadtreeNode remove(Point2D g) {
		getIntersectingRegion(g).remove(g);

		if (countWhiteNodes() == 3) {
			int i = 0;
			PRQuadtreeNode currNode = children[i];
			while (currNode.getType() == WHITE) {
				i++;
				currNode = children[i];
			}

			return currNode;
		}

		return this;
	}

	@Override
	public boolean find(Point2D g) {
		return getIntersectingRegion(g).find(g);
	}

	@Override
	public int getType() {
		return GRAY;
	}

	@Override
	public void toXml(Element parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void elementsInRange(Circle2D range, PriorityQueue<Point2D> queue) {
		// TODO Auto-generated method stub

	}

	private PRQuadtreeNode getIntersectingRegion(Point2D point) {
		if (point.getY() < regions[0].getY()) {
			if (point.getX() < regions[0].getMaxX())
				return children[2];
			else
				return children[3];
		} else {
			if (point.getX() < regions[0].getMaxX())
				return children[0];
			else
				return children[1];
		}
	}
}
