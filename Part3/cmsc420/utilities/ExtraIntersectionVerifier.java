package cmsc420.utilities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class ExtraIntersectionVerifier {

	public static boolean intersectsExcludingEndpoints(Line2D l1, Line2D l2) {
		//If the endpoints of this road does not equal the endpoints of the other road then it intersects
		return l1.intersectsLine(l2) && 
				!l1.getP1().equals(l2.getP1()) &&
				!l1.getP1().equals(l2.getP2()) && 
				!l1.getP2().equals(l2.getP1()) && 
				!l1.getP2().equals(l2.getP2());
	}
	
	public static boolean intersectsExcludingEndpoints(Line2D l, Point2D p) {
		return l.ptSegDistSq(p) == 0 && !l.getP1().equals(p) && !l.getP2().equals(p);
	}
	
	public static boolean isEndpoint(Line2D l, Point2D p) {
		return l.getP1().equals(p) || l.getP2().equals(p);
	}
 }
