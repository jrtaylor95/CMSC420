package cmsc420.meeshquest.part1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PRQuadtreeGray extends PRQuadtreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PRQuadtreeNode nw, ne, se, sw;

	protected PRQuadtreeGray(float x, float y) {
		this.x = x;
		this.y = y;
		nw = (PRQuadtreeNode) PRQuadtreeWhite.getInstance();
		ne = (PRQuadtreeNode) PRQuadtreeWhite.getInstance();
		se = (PRQuadtreeNode) PRQuadtreeWhite.getInstance();
		sw = (PRQuadtreeNode) PRQuadtreeWhite.getInstance();
	}
	
	public Element printToXml(Document document) {
		Element gray = document.createElement("gray");
		gray.setAttribute("x", Integer.toString((int) x));
		gray.setAttribute("y", Integer.toString((int) y));
		
		return gray;
	}
	
	public int countWhiteNodes() {
		int count = 0;
		
		if (nw instanceof PRQuadtreeWhite)
			count++;
		if (ne instanceof PRQuadtreeWhite)
			count++;
		if (sw instanceof PRQuadtreeWhite)
			count++;
		if (se instanceof PRQuadtreeWhite)
			count++;
		
		return count;
	}
	
	public PRQuadtreeNode getQuadrant(int x, int y) {
		if (y < this.y) {
			if (x < this.x)
				return sw;
			else
				return se;
		} else {
			if (x < this.x)
				return nw;
			else
				return ne;
		}
	}
}
