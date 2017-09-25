package cmsc420.structure.PMQuadtree;

import java.awt.geom.Rectangle2D;
import java.util.PriorityQueue;

import cmsc420.exceptions.EdgeIntersectsAnotherEdgeException;
import cmsc420.exceptions.InvalidRegionSizeException;
import cmsc420.exceptions.PMRuleViolationException;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;

public interface PMQuadtreeNode {
	public static final int WHITE = 0;
	public static final int GRAY = 1;
	public static final int BLACK = 2;

	static EdgeIntersectsAnotherEdgeException edgeIntersectsAnotherEdgeException = new EdgeIntersectsAnotherEdgeException();
	static PMRuleViolationException pmRuleViolationException = new PMRuleViolationException();
	
	abstract PMQuadtreeNode add(Geometry2D g, Rectangle2D region, PMQuadtreeValidator validator) throws PMRuleViolationException, EdgeIntersectsAnotherEdgeException, InvalidRegionSizeException;

	abstract PMQuadtreeNode remove(Geometry2D g, PMQuadtreeValidator validator);

	abstract boolean find(Geometry2D g);

	public abstract int getType();
	
	abstract void elementsInRange(Circle2D range, PriorityQueue<Geometry2D> queue);
}
