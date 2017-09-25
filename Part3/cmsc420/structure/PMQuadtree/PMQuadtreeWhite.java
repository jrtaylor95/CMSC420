package cmsc420.structure.PMQuadtree;

import java.awt.geom.Rectangle2D;
import java.util.PriorityQueue;

import cmsc420.exceptions.EdgeIntersectsAnotherEdgeException;
import cmsc420.exceptions.InvalidRegionSizeException;
import cmsc420.exceptions.PMRuleViolationException;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;

public class PMQuadtreeWhite implements PMQuadtreeNode {
	private final static PMQuadtreeWhite INSTANCE = new PMQuadtreeWhite();

	public static PMQuadtreeWhite getInstance() {
		return INSTANCE;
	}

	@Override
	public PMQuadtreeNode add(Geometry2D g, Rectangle2D region, PMQuadtreeValidator validator) throws PMRuleViolationException, EdgeIntersectsAnotherEdgeException, InvalidRegionSizeException {
		PMQuadtreeBlack node = new PMQuadtreeBlack();
		node.add(g, region, validator);
		
		return node;
	}

	@Override
	public PMQuadtreeNode remove(Geometry2D g, PMQuadtreeValidator validator) {
		return this;
	}

	@Override
	public boolean find(Geometry2D g) {
		return false;
	}

	@Override
	public void elementsInRange(Circle2D range, PriorityQueue<Geometry2D> queue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getType() {
		return WHITE;
	}
}
