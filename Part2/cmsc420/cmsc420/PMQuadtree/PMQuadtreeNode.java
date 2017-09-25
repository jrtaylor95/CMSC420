package cmsc420.PMQuadtree;

import java.awt.geom.Rectangle2D;
import java.util.PriorityQueue;

import org.w3c.dom.Element;

import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;

public abstract class PMQuadtreeNode {
	public static final int WHITE = 0;
	public static final int GRAY = 1;
	public static final int BLACK = 2;

	abstract PMQuadtreeNode add(Geometry2D g, Rectangle2D region, PMQuadtreeValidator validator);

	abstract PMQuadtreeNode remove(Geometry2D g, PMQuadtreeValidator validator);

	abstract boolean find(Geometry2D g);

	public abstract int getType();

	abstract void toXml(Element parent);

	abstract void elementsInRange(Circle2D range, PriorityQueue<Geometry2D> queue);
}
