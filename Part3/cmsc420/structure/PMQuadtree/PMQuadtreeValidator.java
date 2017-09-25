package cmsc420.structure.PMQuadtree;

import cmsc420.exceptions.EdgeIntersectsAnotherEdgeException;
import cmsc420.exceptions.PMRuleViolationException;

public interface PMQuadtreeValidator {
	static EdgeIntersectsAnotherEdgeException edgeIntersectsAnotherEdgeException = new EdgeIntersectsAnotherEdgeException();
	static PMRuleViolationException PMRuleViolationException = new PMRuleViolationException();
	
	boolean isValid(PMQuadtreeBlack node) throws EdgeIntersectsAnotherEdgeException, PMRuleViolationException;
}
