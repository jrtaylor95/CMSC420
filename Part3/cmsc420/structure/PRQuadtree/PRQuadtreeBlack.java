package cmsc420.structure.PRQuadtree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.PriorityQueue;

import org.w3c.dom.Element;

import cmsc420.geom.Circle2D;

public class PRQuadtreeBlack implements PRQuadtreeNode {
	Point2D element;

	public PRQuadtreeBlack() {
		element = null;
	}

	@Override
	public PRQuadtreeNode add(Point2D g, Rectangle2D region) {
		if (element == null) {
			element = g;
			return this;
		}
		
		if (element.equals(g))
			return this;
		
		return partition(g, region);
	}
	
	private PRQuadtreeGray partition(Point2D g, Rectangle2D region) {
		PRQuadtreeGray gray = new PRQuadtreeGray(region);
		gray.add(element, region);
		gray.add(g, region);
		
		return gray;
	}

	@Override
	public PRQuadtreeNode remove(Point2D g) {
		if (element.equals(g))
			return PRQuadtreeWhite.getInstance();
		return this;
	}

	@Override
	public boolean find(Point2D g) {
		return element.equals(g);
	}

	@Override
	public int getType() {
		return BLACK;
	}

	@Override
	public void toXml(Element parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void elementsInRange(Circle2D range, PriorityQueue<Point2D> queue) {
		// TODO Auto-generated method stub
		
	}
}
