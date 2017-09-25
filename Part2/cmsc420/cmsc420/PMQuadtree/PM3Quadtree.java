package cmsc420.PMQuadtree;

import org.w3c.dom.Element;

import cmsc420.geom.Geometry2D;

public class PM3Quadtree extends PMQuadtree {

	public PM3Quadtree(int xMax, int yMax) {
		super(xMax, yMax, new PM3QuadtreeValidator());
	}

	@Override
	public void toXml(Element parent) {
		Element pmElement = parent.getOwnerDocument().createElement("quadtree");

		pmElement.setAttribute("order", "3");

		root.toXml(pmElement);

		parent.appendChild(pmElement);
	}

	private static class PM3QuadtreeValidator implements PMQuadtreeValidator {

		PM3QuadtreeValidator() {

		}

		@Override
		public boolean isValid(PMQuadtreeBlack node) {
			int numPoints = 0;
//			int index = 0;
//			int size = node.objects.size();

			for (Geometry2D g : node.objects) {
				if (g.getType() == Geometry2D.POINT) {
					numPoints++;

					if (numPoints > 1)
						return false;
				}
//				} else if (g.getType() == Geometry2D.SEGMENT) {
//					Collection<Geometry2D> otherG = node.objects.subList(index, size);
//					Iterator<Geometry2D> iter = otherG.iterator();
//
//					//Iterate through the rest of the geometry
//					Geometry2D h;
//					while (iter.hasNext()) {
//						h = iter.next();
//						if (h.getType() == Geometry2D.SEGMENT) {
//							if (((Road) g).intersectsExcludingEndpoints((Road) h))
//								return false;
//						}
//					}
//				}
//				index++;
			}

			return true;
		}

	}

	@Override
	public int getOrder() {
		return 3;
	}
}
