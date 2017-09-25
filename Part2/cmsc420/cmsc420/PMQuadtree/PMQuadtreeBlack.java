package cmsc420.PMQuadtree;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import org.w3c.dom.Element;

import cmsc420.Structure.*;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;

public class PMQuadtreeBlack extends PMQuadtreeNode {
	ArrayList<Geometry2D> objects; 

	public PMQuadtreeBlack() {		
		objects = new ArrayList<Geometry2D>();
	}

	@Override
	public PMQuadtreeNode add(Geometry2D g, Rectangle2D region, PMQuadtreeValidator validator) {
		if (objects.contains(g))
			return this;

		objects.add(g);

		if (!validator.isValid(this))
			return partition(region, validator);

		return this;
	}

	@Override
	public PMQuadtreeNode remove(Geometry2D g, PMQuadtreeValidator validator) {
		objects.remove(g);

		if (objects.isEmpty())
			return PMQuadtreeWhite.getInstance();
		else
			return this;
	}

	public PMQuadtreeNode partition(Rectangle2D region, PMQuadtreeValidator validator) {
		PMQuadtreeGray node = new PMQuadtreeGray(region);

		for (Geometry2D g : objects) {
			node.add(g, region, validator);
		}

		return node;
	}

	public PMQuadtreeNode add(Collection<Geometry2D> objects, Rectangle2D region, PMQuadtreeValidator validator) {
		this.objects.addAll(objects);

		if (!validator.isValid(this))
			return partition(region, validator);

		return this;
	}

	@Override
	boolean find(Geometry2D g) {

		if (g.getType() == Geometry2D.POINT) {
			for (Geometry2D geo : objects) {
				if (((City) g ).equals(geo))
					return true;
			}
		} else {
			for (Geometry2D geo : objects) {
				if (((Road) g).equals(geo))
					return true;
			}
		}


		return false;
	}

	public Collection<Geometry2D> getObjects() {
		return objects;
	}

	@Override
	void toXml(Element parent) {
		Element blackNode = parent.getOwnerDocument().createElement("black");

		blackNode.setAttribute("cardinality", Integer.toString(objects.size()));

		objects.sort(new GeometryNameComparator());

		for (Geometry2D g : objects) {
			if (g.getType() == Geometry2D.POINT) {
				if (((City) g).isIsolated()) {
					Element isolatedCityNode = parent.getOwnerDocument().createElement("isolatedCity");
					isolatedCityNode.setAttribute("name", ((City) g).getName());
					isolatedCityNode.setAttribute("color", ((City) g).getColor());
					isolatedCityNode.setAttribute("x", Integer.toString((int) ((City) g).getX()));
					isolatedCityNode.setAttribute("y", Integer.toString((int) ((City) g).getY()));
					isolatedCityNode.setAttribute("radius", Integer.toString((int) ((City) g).getRadius()));

					blackNode.appendChild(isolatedCityNode);
				} else 
					((City) g).toXml(blackNode);
			} else if (g.getType() == Geometry2D.SEGMENT)
				((Road) g).toXml(blackNode);
		}

		parent.appendChild(blackNode);

	}

	@Override
	void elementsInRange(Circle2D range, PriorityQueue<Geometry2D> queue) {
		for (Geometry2D g : objects) {
			if (g.getType() == Geometry2D.POINT && Inclusive2DIntersectionVerifier.intersects((Point2D) g, range)) {
				if (!queue.contains(g))
					queue.add(g);
			} else if (g.getType() == Geometry2D.SEGMENT) {
				if (((Line2D) g).ptSegDist(range.getCenterX(), range.getCenterY()) <= range.getRadius()) {
					if (!queue.contains(g))
						queue.add(g);
				}
			}
		}
	}


	@Override
	public int getType() {
		return PMQuadtreeNode.BLACK;
	}
}
