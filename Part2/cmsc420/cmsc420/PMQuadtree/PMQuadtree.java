package cmsc420.PMQuadtree;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;

import org.w3c.dom.Element;

import cmsc420.Structure.CoordinateComparator;
import cmsc420.Structure.GeometryNameComparator;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;

public abstract class PMQuadtree {
	int xMax, yMax;
	PMQuadtreeNode root;
	PMQuadtreeValidator validator;
	Rectangle2D region;
	TreeSet<Geometry2D> edgeSet;
	TreeSet<Geometry2D> pointSet;

	PMQuadtree(int xMax, int yMax, PMQuadtreeValidator validator) {
		this.xMax = xMax;
		this.yMax = yMax;
		this.validator = validator;
		root = PMQuadtreeWhite.getInstance();
		region = new Rectangle2D.Double(0, 0, xMax, yMax);
		edgeSet = new TreeSet<Geometry2D>(new GeometryNameComparator());
		pointSet = new TreeSet<Geometry2D>(new CoordinateComparator());
	}

	public TreeSet<Geometry2D> getEdgeSet() {
		return edgeSet;
	}

	public TreeSet<Geometry2D> getPointSet() {
		return pointSet;
	}

	public void insert(Geometry2D g) {
		if (g == null)
			throw new NullPointerException();

		root = root.add(g, region, validator);

		if (g.getType() == Geometry2D.SEGMENT)
			edgeSet.add(g);
		else if (g.getType() == Geometry2D.POINT)
			pointSet.add(g);
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
		edgeSet.clear();
		pointSet.clear();
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

	public abstract void toXml(Element parent);

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
			} else if (currNode.getType() == PMQuadtreeNode.BLACK)
				nearestElements.addAll(((PMQuadtreeBlack) currNode).objects);
		}

		if (nearestElements.isEmpty())
			return null;
		
		return nearestElements.remove();
	}

	public abstract int getOrder();
}
