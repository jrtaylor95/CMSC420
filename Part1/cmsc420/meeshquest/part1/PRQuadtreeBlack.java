package cmsc420.meeshquest.part1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PRQuadtreeBlack<T extends City> extends PRQuadtreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	T element;

	public PRQuadtreeBlack(T element) {
		x = (int) element.x;
		y = (int) element.y;
		this.element = element;
	}
	
	public Element printToXml(Document document) {
		Element black = document.createElement("black");
		black.setAttribute("name", element.getName());
		black.setAttribute("x", Integer.toString((int) element.x));
		black.setAttribute("y", Integer.toString((int) element.y));
		
		return black;
	}
}
