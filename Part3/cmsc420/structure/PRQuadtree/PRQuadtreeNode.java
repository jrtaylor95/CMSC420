package cmsc420.structure.PRQuadtree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.PriorityQueue;

import org.w3c.dom.Element;

import cmsc420.geom.Circle2D;

public interface PRQuadtreeNode {
	public static final int WHITE = 0;
	public static final int GRAY = 1;
	public static final int BLACK = 2;

	abstract PRQuadtreeNode add(Point2D g, Rectangle2D region);

	abstract PRQuadtreeNode remove(Point2D g);

	abstract boolean find(Point2D g);

	public abstract int getType();

	abstract void toXml(Element parent);

	abstract void elementsInRange(Circle2D range, PriorityQueue<Point2D> queue); 
}
