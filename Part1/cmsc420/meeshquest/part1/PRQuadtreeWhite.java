package cmsc420.meeshquest.part1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PRQuadtreeWhite extends PRQuadtreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static PRQuadtreeWhite INSTANCE = new PRQuadtreeWhite();

	private PRQuadtreeWhite() {

	}

	public static PRQuadtreeWhite getInstance() {
		return INSTANCE;
	}
	
	public Element printToXml(Document document) {
		Element white = document.createElement("white");
		
		return white;
	}
}
