package cmsc420.PMQuadtree;

import java.awt.geom.Rectangle2D;
import java.util.PriorityQueue;

import org.w3c.dom.Element;

import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;

public class PMQuadtreeWhite extends PMQuadtreeNode {
	private final static PMQuadtreeWhite INSTANCE = new PMQuadtreeWhite();

	private PMQuadtreeWhite() {
	}

	public static PMQuadtreeWhite getInstance() {
		return INSTANCE;
	}

	@Override
	public PMQuadtreeNode add(Geometry2D g, Rectangle2D region, PMQuadtreeValidator validator) {
		PMQuadtreeBlack node = new PMQuadtreeBlack();
		node.add(g, region, validator);
		
		return node;
	}

	@Override
	public PMQuadtreeNode remove(Geometry2D g, PMQuadtreeValidator validator) {
		throw new UnsupportedOperationException();
	}

	@Override
	boolean find(Geometry2D g) {
		return false;
	}
	
	@Override
	public void toXml(Element parent) {
		parent.appendChild(parent.getOwnerDocument().createElement("white"));
	}

	@Override
	void elementsInRange(Circle2D range, PriorityQueue<Geometry2D> queue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getType() {
		return PMQuadtreeNode.WHITE;
	}
}
