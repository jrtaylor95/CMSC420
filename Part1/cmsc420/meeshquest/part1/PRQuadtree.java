package cmsc420.meeshquest.part1;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Circle2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.xml.XmlUtility;

/*
 * Based on the PRQuadtree implementation from the slides from Virginia Tech
 */
public class PRQuadtree<T extends City> {
	private PRQuadtreeNode root;
	private int xMin, xMax, yMin, yMax;

	public PRQuadtree(int xMin, int xMax, int yMin, int yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;

		root = PRQuadtreeWhite.getInstance();
	}

	public void insert (T ele) {
		root = insertHelper(root, ele, xMin, xMax, yMin, yMax);
	}

	private PRQuadtreeNode insertHelper(PRQuadtreeNode sRoot, T ele, int xLo, int xHi, int yLo, int yHi) {
		PRQuadtreeGray internal;
		int xNew = xHi - (xHi - xLo) / 2;
		int yNew = yHi - (yHi - yLo) / 2;

		if (sRoot instanceof PRQuadtreeWhite)
			sRoot = new PRQuadtreeBlack<T>(ele);
		else if (sRoot instanceof PRQuadtreeBlack) {
			//If the leaf is black

			T temp = ((PRQuadtreeBlack<T>) sRoot).element;
			sRoot = new PRQuadtreeGray(xNew, yNew);

			insertHelper(sRoot, temp, xLo, xHi, yLo, yHi);
			insertHelper(sRoot, ele, xLo, xHi, yLo, yHi);
		} else {
			internal = (PRQuadtreeGray) sRoot;

			if (ele.y < internal.y) {
				if (ele.x < internal.x)
					internal.sw = insertHelper(internal.sw, ele, xLo, xNew, yLo, yNew);
				else
					internal.se = insertHelper(internal.se, ele, xNew, xHi, yLo, yNew);

			} else {
				if (ele.x < internal.x)
					internal.nw = insertHelper(internal.nw, ele, xLo, xNew, yNew, yHi);
				else
					internal.ne = insertHelper(internal.ne, ele, xNew, xHi, yNew, yHi);
			}
		}
		return sRoot;
	}

	public void remove(T ele) {
		root = removeHelper(root, ele);
	}

	private PRQuadtreeNode removeHelper(PRQuadtreeNode sRoot, T ele) {
		//		PRQuadtreeGray gray;
		PRQuadtreeGray internal;

		if (sRoot instanceof PRQuadtreeBlack) {
			if (((PRQuadtreeBlack<T>) sRoot).element.equals(ele))
				//The element is removed
				sRoot = PRQuadtreeWhite.getInstance();
		} else if (sRoot instanceof PRQuadtreeGray) {
			internal = (PRQuadtreeGray) sRoot;
			//			internal = gray.getQuadrant((int) ele.x, (int) ele.y);
			//			internal = removeHelper(internal, ele);

			if (ele.y < internal.y) {
				if (ele.x < internal.x)
					internal.sw = removeHelper(internal.sw, ele);
				else 
					internal.se = removeHelper(internal.se, ele);
			} else {
				if (ele.x < internal.x)
					internal.nw = removeHelper(internal.nw, ele);
				else
					internal.ne = removeHelper(internal.ne, ele);
			}

			if (internal.countWhiteNodes() == 3) {
				if (internal.nw instanceof PRQuadtreeBlack)
					sRoot = internal.nw;
				else if (internal.ne instanceof PRQuadtreeBlack)
					sRoot = internal.ne;
				else if (internal.sw instanceof PRQuadtreeBlack)
					sRoot = internal.sw;
				else if (internal.se instanceof PRQuadtreeBlack)
					sRoot = internal.se;
			} else if (internal.countWhiteNodes() == 4)
				sRoot = PRQuadtreeWhite.getInstance();
		}

		return sRoot;
	}

	public boolean find(T ele) {
		return findHelper(root, ele);
	}

	private boolean findHelper(PRQuadtreeNode sRoot, T ele) {
		PRQuadtreeGray internal;

		if (sRoot instanceof PRQuadtreeWhite)
			return false;
		else if (sRoot instanceof PRQuadtreeBlack)
			return ((PRQuadtreeBlack<T>) sRoot).element.equals(ele);
		else {
			internal = (PRQuadtreeGray) sRoot;
			//			internal = ((PRQuadtreeGray) sRoot).getQuadrant((int) ele.x, (int) ele.y);
			//			return findHelper(internal, ele);
			if (ele.y < internal.y) {
				if (ele.x < internal.x)
					return findHelper(internal.sw, ele);
				else 
					return findHelper(internal.se, ele);
			} else {
				if (ele.x < internal.x)
					return findHelper(internal.nw, ele);
				else
					return findHelper(internal.ne, ele);
			}
		}
	}

	public void clear() {
		root = PRQuadtreeWhite.getInstance();
	}

	public boolean isInBounds(T ele) {
		return ele.x >= xMin && ele.x < xMax && ele.y >= yMin && ele.y < yMax;
	}

	public boolean isEmpty() {
		return root instanceof PRQuadtreeWhite;
	}

	public Element printToXml(Document results) {
		Element quadtree = results.createElement("quadtree");
		printToXmlHelper(results, quadtree, root);
		return quadtree;
	}

	private void printToXmlHelper(Document results, Element parent, PRQuadtreeNode sRoot) {
		PRQuadtreeGray internal;
		Element node;

		if (sRoot instanceof PRQuadtreeWhite)
			node = ((PRQuadtreeWhite) sRoot).printToXml(results);
		else if (sRoot instanceof PRQuadtreeBlack)
			node = ((PRQuadtreeBlack<T>) sRoot).printToXml(results);
		else {
			internal = (PRQuadtreeGray) sRoot;
			node = internal.printToXml(results);
			printToXmlHelper(results, node, internal.nw);
			printToXmlHelper(results, node, internal.ne);
			printToXmlHelper(results, node, internal.sw);
			printToXmlHelper(results, node, internal.se);
		}
		parent.appendChild(node);
	}

	public CanvasPlus printToCanvas() {
		CanvasPlus canvas = new CanvasPlus("MeeshQuest", xMax, yMax);
		canvas.addRectangle(0, 0, xMax, yMax, Color.BLACK, false);
		printToCanvasHelper(canvas, root, xMin, xMax, yMin, yMax);

		return canvas;
	}

	public void printToCanvas(String name) throws IOException {
		CanvasPlus canvas = printToCanvas();

		canvas.save(name);
		canvas.dispose();
	}

	private void printToCanvasHelper(CanvasPlus canvas, PRQuadtreeNode sRoot, int xLo, int xHi, int yLo, int yHi) {
		PRQuadtreeBlack<T> leaf;
		PRQuadtreeGray internal;
		int xNew = xHi - (xHi - xLo) / 2;
		int yNew = yHi - (yHi - yLo) / 2;

		if (sRoot instanceof PRQuadtreeBlack) {
			leaf = (PRQuadtreeBlack<T>) sRoot;
			canvas.addPoint(leaf.element.getName(), leaf.element.x, leaf.element.y, Color.BLACK);
		} else if (sRoot instanceof PRQuadtreeGray) {
			internal = (PRQuadtreeGray) sRoot;
			canvas.addCross(internal.x, internal.y, (xHi - xLo) / 2, Color.BLACK);
			printToCanvasHelper(canvas, internal.nw, xNew, xHi, yNew, yHi);
			printToCanvasHelper(canvas, internal.ne, xLo, xNew, yNew, yHi);
			printToCanvasHelper(canvas, internal.sw, xNew, xHi, yLo, yNew);
			printToCanvasHelper(canvas, internal.se, xLo, xNew, yLo, yNew);
		}
	}

	public PriorityQueue<T> getCitiesInRange(int x, int y, int radius) {
		Circle2D circle = new Circle2D.Float(x, y, radius);

		PriorityQueue<T> inRange = new PriorityQueue<T>(new CityNameComparator());
		getCitiesInRangeHelper(root, inRange, circle);

		return inRange;
	}

	private void getCitiesInRangeHelper(PRQuadtreeNode sRoot, PriorityQueue<T> inRange, Circle2D circle) {
		PRQuadtreeBlack<T> leaf;
		PRQuadtreeGray internal;

		if (sRoot instanceof PRQuadtreeBlack) {
			leaf = (PRQuadtreeBlack<T>) sRoot;
			if (Inclusive2DIntersectionVerifier.intersects(leaf.element, circle))
				inRange.add(leaf.element);	
		} else if (sRoot instanceof PRQuadtreeGray) {
			internal = (PRQuadtreeGray) sRoot;
			getCitiesInRangeHelper(internal.ne, inRange, circle);
			getCitiesInRangeHelper(internal.nw, inRange, circle);
			getCitiesInRangeHelper(internal.se, inRange, circle);
			getCitiesInRangeHelper(internal.sw, inRange, circle);
		}
	}

	public PriorityQueue<T> getCitiesInRange(int x, int y, int radius, String saveMap) throws IOException {
		PriorityQueue<T> inRange = getCitiesInRange(x, y, radius);

		if (!inRange.isEmpty()) {
			CanvasPlus canvas = printToCanvas();
			canvas.addCircle(x, y, radius, Color.blue, false);
			canvas.save(saveMap);
			canvas.dispose();
		}
		return inRange;
	}

	public T getNearest(int x, int y) {
		PRQuadtreeNode ele;
		PriorityQueue<PRQuadtreeNode> queue = new PriorityQueue<PRQuadtreeNode>(new CoordinateDistanceComparator<PRQuadtreeNode>(x, y));
		queue.add(root);

		while (!queue.isEmpty()) {
			ele = queue.remove();
			if (ele instanceof PRQuadtreeBlack)
				return ((PRQuadtreeBlack<T>) ele).element;
			else if (ele instanceof PRQuadtreeGray) {
				if (!(((PRQuadtreeGray) ele).ne instanceof PRQuadtreeWhite))
					queue.add(((PRQuadtreeGray) ele).ne);
				if (!(((PRQuadtreeGray) ele).nw instanceof PRQuadtreeWhite))
					queue.add(((PRQuadtreeGray) ele).nw);
				if (!(((PRQuadtreeGray) ele).se instanceof PRQuadtreeWhite))
					queue.add(((PRQuadtreeGray) ele).se);
				if (!(((PRQuadtreeGray) ele).sw instanceof PRQuadtreeWhite))
					queue.add(((PRQuadtreeGray) ele).sw);
			}
		}

		return null;
	}
}
