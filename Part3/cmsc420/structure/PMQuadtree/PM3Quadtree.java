package cmsc420.structure.PMQuadtree;

import java.awt.geom.Line2D;

import cmsc420.SpatialMapLine;
import cmsc420.exceptions.EdgeIntersectsAnotherEdgeException;
import cmsc420.exceptions.PMRuleViolationException;
import cmsc420.structure.SpatialMapPoint;
import cmsc420.utilities.ExtraIntersectionVerifier;

public class PM3Quadtree extends PMQuadtree {

	public PM3Quadtree(int xMax, int yMax) {
		super(xMax, yMax, PM3QuadtreeValidator.getInstance());
	}

	@Override
	public int getOrder() {
		return 3;
	}
}

class PM3QuadtreeValidator implements PMQuadtreeValidator {
	private static final PM3QuadtreeValidator INSTANCE = new PM3QuadtreeValidator();

	@Override
	public boolean isValid(PMQuadtreeBlack node) throws EdgeIntersectsAnotherEdgeException, PMRuleViolationException {
		for (SpatialMapPoint point : node.getPoints()) {
			int index = 1;
			for (SpatialMapLine g : node.getEdges()) {
				//Iterate through the rest of the geometry
				for (SpatialMapLine h : node.getEdges().subList(index, node.getEdges().size())) {
					if (ExtraIntersectionVerifier.intersectsExcludingEndpoints((Line2D) g, (Line2D) h))
						throw edgeIntersectsAnotherEdgeException;
				}
				if (ExtraIntersectionVerifier.intersectsExcludingEndpoints((Line2D) g, point))
					throw PMRuleViolationException;
				index++;
			}
		}

		return node.getPoints().size() <= 1;
	}

	public static PM3QuadtreeValidator getInstance() {
		return INSTANCE;
	}

}
