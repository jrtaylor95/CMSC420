package cmsc420.structure.PMQuadtree;


import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.PriorityQueue;

import cmsc420.exceptions.EdgeIntersectsAnotherEdgeException;
import cmsc420.exceptions.InvalidRegionSizeException;
import cmsc420.exceptions.PMRuleViolationException;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;

public class PMQuadtreeGray implements PMQuadtreeNode {

	private final int NUM_REGIONS = 4;

	public PMQuadtreeNode[] children;
	Rectangle2D[] regions;

	private static InvalidRegionSizeException invalidRegionSizeException = new InvalidRegionSizeException();

	protected PMQuadtreeGray(Rectangle2D region) throws InvalidRegionSizeException {
		double halfHeight = region.getHeight() * .5;
		double halfWidth = region.getWidth() * .5;
		if (halfHeight < 1 || halfWidth < 1)
			throw invalidRegionSizeException;
		double minX = region.getMinX();
		double minY = region.getMinY();
		double centerX = region.getCenterX();
		double centerY = region.getCenterY();
		children = new PMQuadtreeNode[NUM_REGIONS];
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
			children[i] = PMQuadtreeWhite.getInstance();
	}

	public int countWhiteNodes() {
		int count = 0;

		for (int i = 0; i < NUM_REGIONS; i++) {
			if (children[i].getType() == PMQuadtreeNode.WHITE)
				count++;
		}

		return count;
	}

	public int countGrayNodes() {
		int count = 0;

		for (int i = 0; i < NUM_REGIONS; i++) {
			if (children[i].getType() == PMQuadtreeNode.GRAY)
				count++;
		}

		return count;
	}

	@Override
	public PMQuadtreeNode add(Geometry2D g, Rectangle2D region, PMQuadtreeValidator validator) throws PMRuleViolationException, EdgeIntersectsAnotherEdgeException, InvalidRegionSizeException {
		boolean intersects = false;

		for (int i = 0; i < NUM_REGIONS; i++) {
			intersects = false;
			if (g.getType() == Geometry2D.SEGMENT)
				intersects = Inclusive2DIntersectionVerifier.intersects((Line2D) g, regions[i]);
			else if (g.getType() == Geometry2D.POINT) 
				intersects = Inclusive2DIntersectionVerifier.intersects((Point2D) g, regions[i]);

			if (intersects)
				children[i] = children[i].add(g, regions[i], validator);
		}

		return this;

	}

	@Override
	public PMQuadtreeNode remove(Geometry2D g, PMQuadtreeValidator validator) {
		int whiteNodes;
		boolean intersects = false;

		for (int i = 0; i < NUM_REGIONS; i++) {
			if (g.getType() == Geometry2D.SEGMENT)
				intersects = Inclusive2DIntersectionVerifier.intersects((Line2D) g, regions[i]);
			else if (g.getType() == Geometry2D.POINT)
				intersects = Inclusive2DIntersectionVerifier.intersects((Point2D) g, regions[i]);

			if (intersects)
				children[i] = children[i].remove(g, validator);
		}

		whiteNodes = countWhiteNodes();
		if (whiteNodes == NUM_REGIONS)
			return PMQuadtreeWhite.getInstance();
		else if (whiteNodes == NUM_REGIONS - 1) {
			for (int i = 0; i < NUM_REGIONS; i++) {
				if (children[i].getType() == PMQuadtreeNode.BLACK)
					return children[i];
			}
		} else if (countGrayNodes() == 0)
			return merge(validator);

		return this;
	}
	
	private PMQuadtreeNode merge(PMQuadtreeValidator validator) {
		PMQuadtreeBlack newNode = new PMQuadtreeBlack();
		
		for (int i = 0; i < NUM_REGIONS; i++) {
			if (children[i].getType() == PMQuadtreeNode.BLACK)
				newNode = newNode.add(((PMQuadtreeBlack) children[i]));
			
			if (newNode == null)
				return this;
		}
		
		try {
			if (validator.isValid(newNode))
				return newNode;
			else
				return this;
		} catch (EdgeIntersectsAnotherEdgeException | PMRuleViolationException e) {
			return this;
		}
	}

	@Override
	public boolean find(Geometry2D g) {
		boolean intersects = false;

		for (int i = 0; i < NUM_REGIONS; i++) {
			if (g.getType() == Geometry2D.SEGMENT)
				intersects = Inclusive2DIntersectionVerifier.intersects((Line2D) g, regions[i]);
			else if (g.getType() == Geometry2D.POINT)
				intersects = Inclusive2DIntersectionVerifier.intersects((Point2D) g, regions[i]);

			if (intersects)
				return children[i].find(g);
		}

		return false;
	}

	//	@Override
	//	public void toXml(Element parent) {
	//		Element grayElement = parent.getOwnerDocument().createElement("gray");
	//
	//		//Get the center point of the whole region
	//		grayElement.setAttribute("x", Integer.toString((int) regions[0].getMaxX()));
	//		grayElement.setAttribute("y", Integer.toString((int) regions[0].getMinY()));
	//
	//		for (int i = 0; i < NUM_REGIONS; i++)
	//			children[i].toXml(grayElement);
	//
	//		parent.appendChild(grayElement);
	//	}

	@Override
	public void elementsInRange(Circle2D range, PriorityQueue<Geometry2D> queue) {
		for (int i = 0; i < NUM_REGIONS; i++) {
			if (children[i].getType() != PMQuadtreeNode.WHITE)
				children[i].elementsInRange(range, queue);
		}

	}

	@Override
	public int getType() {
		return PMQuadtreeNode.GRAY;
	}

	public Rectangle2D getRegion(int i) {
		return regions[i];
	}
	
	public PMQuadtreeNode getChild(int i) {
		return children[i];
	}
}
