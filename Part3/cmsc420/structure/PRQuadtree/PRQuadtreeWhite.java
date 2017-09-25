package cmsc420.structure.PRQuadtree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.PriorityQueue;

import org.w3c.dom.Element;

import cmsc420.geom.Circle2D;

public class PRQuadtreeWhite implements PRQuadtreeNode {
	private final static PRQuadtreeWhite INSTANCE = new PRQuadtreeWhite();

	private PRQuadtreeWhite() {
		
	}

	public static PRQuadtreeWhite getInstance() {
		return INSTANCE;
	}

	@Override
	public PRQuadtreeNode add(Point2D g, Rectangle2D region) {
		PRQuadtreeBlack black = new PRQuadtreeBlack();
		
		black.add(g, region);
		
		return black;
	}

	@Override
	public PRQuadtreeNode remove(Point2D g) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean find(Point2D g) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getType() {
		return WHITE;
	}

	@Override
	public void toXml(Element parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void elementsInRange(Circle2D range, PriorityQueue<Point2D> queue) {
		throw new UnsupportedOperationException(); 
	}
}
