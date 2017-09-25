package cmsc420.structure.PMQuadtree;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import cmsc420.SpatialMapLine;
import cmsc420.exceptions.EdgeIntersectsAnotherEdgeException;
import cmsc420.exceptions.InvalidRegionSizeException;
import cmsc420.exceptions.PMRuleViolationException;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.structure.SpatialMapPoint;

public class PMQuadtreeBlack implements PMQuadtreeNode {
	private ArrayList<SpatialMapPoint> points;
	private ArrayList<SpatialMapLine> edges; 

	public PMQuadtreeBlack() {
		points = new ArrayList<SpatialMapPoint>();
		edges = new ArrayList<SpatialMapLine>();
	}

	@Override
	public PMQuadtreeNode add(Geometry2D g, Rectangle2D region, PMQuadtreeValidator validator) throws PMRuleViolationException, EdgeIntersectsAnotherEdgeException, InvalidRegionSizeException {
		if (g.getType() == Geometry2D.POINT) {
			if (!points.contains(g))
				points.add((SpatialMapPoint) g);
		} else {
			if (!edges.contains(g))
				edges.add((SpatialMapLine) g);
		}

		if (!validator.isValid(this))
			return partition(region, validator);
		return this;
	}

	public PMQuadtreeBlack add(PMQuadtreeBlack node) {
		if (node.getPoint() != null && !points.isEmpty() && !node.getPoint().equals(points.get(0)))
			return null;

		if (points.isEmpty() && node.getPoint() != null)
			points.add(node.getPoint());
		for (SpatialMapLine edge : node.getEdges()) {
			if (!edges.contains(edge))
				edges.add(edge);
		}

		return this;
	}

	@Override
	public PMQuadtreeNode remove(Geometry2D g, PMQuadtreeValidator validator) {
		if (g.getType() == Geometry2D.POINT) {
			points.remove(g);
		} else
			edges.remove((SpatialMapLine) g);

		return edges.isEmpty() && points.isEmpty() ? PMQuadtreeWhite.getInstance() : this;
	}

	private PMQuadtreeGray partition(Rectangle2D region, PMQuadtreeValidator validator) throws PMRuleViolationException, EdgeIntersectsAnotherEdgeException, InvalidRegionSizeException {
		PMQuadtreeNode node = new PMQuadtreeGray(region);

		for (SpatialMapPoint g : points) {
			node = node.add(g, region, validator);
		}

		for (Geometry2D g : edges) {
			node = node.add(g, region, validator);
		}

		return (PMQuadtreeGray) node;
	}

	public PMQuadtreeNode add(Collection<SpatialMapLine> objects, Rectangle2D region, PMQuadtreeValidator validator) throws PMRuleViolationException, EdgeIntersectsAnotherEdgeException, InvalidRegionSizeException {
		this.edges.addAll(objects);

		if (!validator.isValid(this))
			return partition(region, validator);

		return this;
	}

	@Override
	public boolean find(Geometry2D g) {

		if (g.getType() == Geometry2D.POINT) {
			return ((Point2D) g).equals((Point2D) points.get(0));
		} else {
			for (Geometry2D geo : edges) {
				Line2D line1 = (Line2D) g;
				Line2D line2 = (Line2D) geo;
				if (line1.getP1().equals(line2.getP1()) && line1.getP2().equals(line2.getP2()))
					return true;
			}
		}

		return false;
	}

	public SpatialMapPoint getPoint() {
		return !points.isEmpty() ? points.get(0) : null;
	}
	public ArrayList<SpatialMapLine> getEdges() {
		return edges;
	}

	@Override
	public void elementsInRange(Circle2D range, PriorityQueue<Geometry2D> queue) {
		if (Inclusive2DIntersectionVerifier.intersects((Point2D) points.get(0), range))
			queue.add(points.get(0));

		for (Geometry2D g : edges) {
			if (((Line2D) g).ptSegDist(range.getCenterX(), range.getCenterY()) <= range.getRadius()) {
				if (!queue.contains(g))
					queue.add(g);
			}
		}
	}

	@Override
	public int getType() {
		return PMQuadtreeNode.BLACK;
	}

	public int getCardinality() {
		int cardinality = 0;

		if (!points.isEmpty())
			cardinality += 1;
		cardinality += edges.size();
		return cardinality;
	}
	
	public ArrayList<SpatialMapPoint> getPoints() {
		return points;
	}
}
