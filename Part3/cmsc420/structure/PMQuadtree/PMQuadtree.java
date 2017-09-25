package cmsc420.structure.PMQuadtree;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.PriorityQueue;

import cmsc420.exceptions.EdgeIntersectsAnotherEdgeException;
import cmsc420.exceptions.InvalidRegionSizeException;
import cmsc420.exceptions.PMRuleViolationException;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;

public abstract class PMQuadtree {
	int xMax, yMax;
	PMQuadtreeNode root;
	PMQuadtreeValidator validator;
	Rectangle2D region;

	PMQuadtree(int xMax, int yMax, PMQuadtreeValidator validator) {
		this.xMax = xMax;
		this.yMax = yMax;
		this.validator = validator;
		root = PMQuadtreeWhite.getInstance();
		region = new Rectangle2D.Double(0, 0, xMax, yMax);
	}

	public void insert(Geometry2D g) throws PMRuleViolationException, EdgeIntersectsAnotherEdgeException, InvalidRegionSizeException {
		if (g == null)
			throw new NullPointerException();

		try { 
			root = root.add(g, region, validator);
		} catch (PMRuleViolationException | EdgeIntersectsAnotherEdgeException | InvalidRegionSizeException e) {
			root = root.remove(g, validator);
			throw e;
		}
	}

	public void remove(Geometry2D g) {
		if (g == null)
			throw new NullPointerException();

		root = root.remove(g, validator);
	}

	public boolean isEmpty() {
		return root.getType() == PMQuadtreeNode.WHITE;
	}

	public void clear() {
		root = PMQuadtreeWhite.getInstance();
	}

	public boolean find(Geometry2D g) {
		return root.find(g);
	}

	public boolean isInBounds(Geometry2D g) {		
		if (g.getType() == Geometry2D.POINT)
			return Inclusive2DIntersectionVerifier.intersects((Point2D) g, region);
		else if (g.getType() == Geometry2D.SEGMENT)
			return Inclusive2DIntersectionVerifier.intersects((Line2D) g, region);

		return false;
	}

	public int getMaxX() {
		return xMax;
	}

	public int getMaxY() {
		return yMax;
	}

	public PMQuadtreeNode getRoot() {
		return root;
	}

	public PriorityQueue<Geometry2D> elementsInRange(int x, int y, int radius, Comparator<Geometry2D> comparator) {
		Circle2D range = new Circle2D.Float(x, y, radius);

		PriorityQueue<Geometry2D> inRange = new PriorityQueue<Geometry2D>(comparator);
		if (root.getType() != PMQuadtreeNode.WHITE)
			root.elementsInRange(range, inRange);

		return inRange;
	}

	public Geometry2D nearestElement(Comparator<Geometry2D> comparator) {
		PriorityQueue<PMQuadtreeNode> nearestRegions = new PriorityQueue<PMQuadtreeNode>(new NodeComparator());
		PriorityQueue<Geometry2D> nearestElements = new PriorityQueue<Geometry2D>(comparator);

		nearestRegions.add(root);

		PMQuadtreeNode currNode = null;

		while (!nearestRegions.isEmpty()) {
			currNode = nearestRegions.remove();

			if (currNode.getType() == PMQuadtreeNode.GRAY) {
				for (int i = 0; i < 4; i++)
					nearestRegions.add(((PMQuadtreeGray) currNode).children[i]);
			} else if (currNode.getType() == PMQuadtreeNode.BLACK) {
				if (((PMQuadtreeBlack) currNode).getPoint() != null)
						nearestElements.add(((PMQuadtreeBlack) currNode).getPoint());
				nearestElements.addAll(((PMQuadtreeBlack) currNode).getEdges());
			}
		}

		if (nearestElements.isEmpty())
			return null;

		return nearestElements.remove();
	}

	public abstract int getOrder();
}
