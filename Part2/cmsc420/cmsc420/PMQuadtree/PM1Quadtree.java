package cmsc420.PMQuadtree;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import org.w3c.dom.Element;

import cmsc420.geom.Geometry2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;

public class PM1Quadtree extends PMQuadtree {

	public PM1Quadtree(int xMax, int yMax) {
		super(xMax, yMax, new PM1QuadtreeValidator());

	}

	@Override
	public void toXml(Element parent) {
		Element pmElement = parent.getOwnerDocument().createElement("quadtree");

		pmElement.setAttribute("order", "1");

		root.toXml(pmElement);

		parent.appendChild(pmElement);
	}

	private static class PM1QuadtreeValidator implements PMQuadtreeValidator {

		PM1QuadtreeValidator() {

		}

		@Override
		public boolean isValid(PMQuadtreeBlack node) {
			int numPoints = 0;
			int numLines = 0;
			int index = 0;
			int size = node.objects.size();
			Collection<Geometry2D> otherG;
			Iterator<Geometry2D> iter;

			for (Geometry2D g : node.objects) {
				if (g.getType() == Geometry2D.POINT) {
					numPoints++;

					if (numPoints > 1)
						return false;

					otherG = node.objects.subList(index, size);
					iter = otherG.iterator();

					while (iter.hasNext()) {
						Geometry2D h = iter.next();
						if (h.getType() == Geometry2D.SEGMENT) {
							if (Inclusive2DIntersectionVerifier.intersects((Point2D) g, (Line2D) h))
								return false;
						}
					}
				} else if (g.getType() == Geometry2D.SEGMENT) {
					numLines++;

					otherG = node.objects.subList(index, size);
					iter = otherG.iterator();

					Geometry2D h;
					while (iter.hasNext()) {
						h = iter.next();
						if (h.getType() == Geometry2D.SEGMENT) {
							if (Inclusive2DIntersectionVerifier.intersects((Line2D) g, (Line2D) h))
								return false;
						}
					}
				}
				index++;
			}

			if (numPoints == 0 && numLines > 1)
				return false;

			return true;
		}

	}

	@Override
	public int getOrder() {
		return 1;
	}
}
