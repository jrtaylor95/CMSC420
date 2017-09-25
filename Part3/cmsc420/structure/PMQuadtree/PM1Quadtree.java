package cmsc420.structure.PMQuadtree;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import cmsc420.SpatialMapLine;
import cmsc420.exceptions.EdgeIntersectsAnotherEdgeException;
import cmsc420.exceptions.PMRuleViolationException;
import cmsc420.structure.SpatialMapPoint;
import cmsc420.utilities.ExtraIntersectionVerifier;

public class PM1Quadtree extends PMQuadtree {

	public PM1Quadtree(int xMax, int yMax) {
		super(xMax, yMax, PM1QuadtreeValidator.getInstance());
	}

	@Override
	public int getOrder() {
		return 1;
	}
}

class PM1QuadtreeValidator implements PMQuadtreeValidator {
	private static final PM1QuadtreeValidator INSTANCE = new PM1QuadtreeValidator();

	@Override
	public boolean isValid(PMQuadtreeBlack node) throws EdgeIntersectsAnotherEdgeException, PMRuleViolationException {
		if (node.getPoints().size() > 0) {

			for (SpatialMapPoint point : node.getPoints()) {
				int index = 1;
				for (SpatialMapLine edge : node.getEdges()) {
					for (SpatialMapLine h : node.getEdges().subList(index, node.getEdges().size())) {
						//Check if the lines intersects
						if (ExtraIntersectionVerifier.intersectsExcludingEndpoints((Line2D) edge, (Line2D) h))
							throw edgeIntersectsAnotherEdgeException;
						//Check if the endpoints intersects the line
						if (ExtraIntersectionVerifier.intersectsExcludingEndpoints((Line2D) edge, ((Line2D) edge).getP1()))
							throw edgeIntersectsAnotherEdgeException;
						if (ExtraIntersectionVerifier.intersectsExcludingEndpoints((Line2D) edge, ((Line2D) edge).getP2()))
							throw edgeIntersectsAnotherEdgeException;
					}

					//Check if the point is anywhere on the line but the end points
					if (ExtraIntersectionVerifier.intersectsExcludingEndpoints((Line2D) edge, (Point2D) point))
						throw PMRuleViolationException;
					//Check if the point is not one of the end points
					if (!ExtraIntersectionVerifier.isEndpoint((Line2D) edge, (Point2D) point))
						return false;
					index++;
				}
			}
			if (node.getPoints().size() > 1)
				return false;
		} else {
			if (node.getEdges().size() != 1)
				return false;
		}

		return true;
	}

	public static PM1QuadtreeValidator getInstance() {
		return INSTANCE;
	}

}
