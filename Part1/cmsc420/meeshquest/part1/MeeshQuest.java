package cmsc420.meeshquest.part1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cmsc420.drawing.CanvasPlus;
import cmsc420.xml.XmlUtility;

public class MeeshQuest {
	static TreeMap<String, City> stringCityMap;
	static TreeSet<City> citySet;
	static AvlTree cityTree;
	static Document results;
	static PRQuadtree<City> spatialMap;

	/**
	 * Creates a city with the specified name, coordinates, radius, and color.
	 * @param name
	 * 		The case-sensitive name of the city
	 * @param x
	 * 		The X coordinate of the city
	 * @param y
	 * 		The Y coordinate of the city
	 * @param radius
	 * 		The radius of the city. No other city can be in the radius of another city
	 * @param color
	 * 		The color that the city takes on the map
	 * 		
	 */
	private static void createCity(NamedNodeMap nnm) {
		String name = nnm.getNamedItem("name").getNodeValue();
		int x = Integer.parseInt(nnm.getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(nnm.getNamedItem("y").getNodeValue());
		int radius = Integer.parseInt(nnm.getNamedItem("radius").getNodeValue());
		String color = nnm.getNamedItem("color").getNodeValue();

		City c = new City(name, x, y, radius, color);

		if (citySet.add(c) != false) {
			if (!stringCityMap.containsKey(name)) {
				stringCityMap.put(name, c);

				XmlResultsPrinter.createCitySuccess(results, name, x, y, radius, color);
			} else {
				citySet.remove(c);
				XmlResultsPrinter.createCityError(results, "duplicateCityName", name, x, y, radius, color);
			}
		} else
			XmlResultsPrinter.createCityError(results, "duplicateCityCoordinates", name, x, y, radius, color);
	}

	/**
	 * Deletes a city with the specified name. NOTE: If a city has been mapped, it must first be removed from
	 * the PR Quadtree.
	 * @param name
	 * 		The name of the city to be deleted
	 * 
	 */
	private static void deleteCity(NamedNodeMap nnm) {
		String name = nnm.getNamedItem("name").getNodeValue();

		City c = stringCityMap.remove(name);
		boolean cityUnmapped = false;

		if (c != null) {
			if (spatialMap.find(c)) {
				spatialMap.remove(c);
				cityUnmapped = true;
			}
			citySet.remove(c);

			XmlResultsPrinter.deleteCitySuccess(results, c, cityUnmapped);
		} else
			XmlResultsPrinter.deleteCityError(results, "cityDoesNotExist", name);
	}

	/**
	 * Resets all of the structures
	 */
	private static void clearAll() {
		stringCityMap.clear();
		citySet.clear();
		spatialMap.clear();
		cityTree.clear();

		XmlResultsPrinter.clearAllSuccess(results);
	}

	/**
	 * Prints all cities in the dictionary. Order is determined by sortBy parameter. Coordinate ordering is done 
	 * by Y coordinate first
	 * @param sortBy
	 * 		The order to sort the cities. Valid orders are "name" and "coordinate"
	 */
	private static void listCities(NamedNodeMap nnm) {
		String sortBy = nnm.getNamedItem("sortBy").getNodeValue();

		if (!stringCityMap.isEmpty() && !citySet.isEmpty())
			XmlResultsPrinter.listCitiesSuccess(results, stringCityMap, citySet, sortBy);
		else 
			XmlResultsPrinter.listCitiesError(results, "noCitiesToList", sortBy);
	}

	/**
	 * Inserts the city into the spatial map
	 * @param name
	 * 		The name of the city to map
	 */
	private static void mapCity(NamedNodeMap nnm) {
		String name = nnm.getNamedItem("name").getNodeValue();
		City c = stringCityMap.get(name);
		
		if (c != null) {
			if (!spatialMap.find(c)) {
				if (spatialMap.isInBounds(c)) {
					spatialMap.insert(c);
					cityTree.insert(c);

					XmlResultsPrinter.mapCitySuccess(results, name);
				} else
					XmlResultsPrinter.mapCityError(results, "cityOutOfBounds", name);
			} else
				XmlResultsPrinter.mapCityError(results, "cityAlreadyMapped", name);
		} else
			XmlResultsPrinter.mapCityError(results, "nameNotInDictionary", name);
	}

	/**
	 * Removes the city from the spatial map
	 * @param name
	 * 		The name of the city to remove
	 */
	private static void unmapCity(NamedNodeMap nnm) {
		String name = nnm.getNamedItem("name").getNodeValue();
		City c = stringCityMap.get(name);
		
		if (c != null) {
			if (spatialMap.find(c)) {
				spatialMap.remove(c);

				XmlResultsPrinter.unmapCitySuccess(results, name);
			} else
				XmlResultsPrinter.unmapCityError(results, "cityNotMapped", name);
		} else
			XmlResultsPrinter.unmapCityError(results, "nameNotInDictionary", name);
	}

	/**
	 * Prints the PR Quadtree
	 */
	private static void printPRQuadtree() {
		if (!spatialMap.isEmpty())
			XmlResultsPrinter.printPRQuadtreeSuccess(results, spatialMap);
		else
			XmlResultsPrinter.printPRQuadtreeError(results, "mapIsEmpty");
	}

	/**
	 * Saves the map to a file
	 * @param name
	 * 		The name of the file
	 */
	private static void saveMap(NamedNodeMap nnm) throws IOException{
		String name = nnm.getNamedItem("name").getNodeValue();

		spatialMap.printToCanvas(name);

		XmlResultsPrinter.saveMapSuccess(results, name);
	}

	/**
	 * Lists the cities within the radius of coordinates (x,y) in the spatial map. Cities on the boundary, and at (x,y) are
	 * included.
	 * @param x
	 * @param y
	 * @param radius
	 */
	private static void rangeCities(NamedNodeMap nnm) throws IOException {
		int x = Integer.parseInt(nnm.getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(nnm.getNamedItem("y").getNodeValue());
		int radius = Integer.parseInt(nnm.getNamedItem("radius").getNodeValue());
		String name = nnm.getNamedItem("saveMap") != null ? nnm.getNamedItem("saveMap").getNodeValue() : null;
		PriorityQueue<City> inRange;

		if (name != null) {
			inRange = spatialMap.getCitiesInRange(x, y, radius, name);
		} else
			inRange = spatialMap.getCitiesInRange(x, y, radius);

		if (!inRange.isEmpty()) {
			if (name == null)
				XmlResultsPrinter.rangeCitiesSuccess(results, inRange, x, y, radius);
			else
				XmlResultsPrinter.rangeCitiesSuccess(results, inRange, x, y, radius, name);
		} else {
			if (name == null)
				XmlResultsPrinter.rangeCitiesError(results, "noCitiesExistInRange", x, y, radius);
			else
				XmlResultsPrinter.rangeCitiesError(results, "noCitiesExistInRange", x, y, radius, name);
		}
	}

	/**
	 * Returns the name and location of the nearest city to points (x,y) on the spatial map.
	 * @param x
	 * @param y
	 */
	private static void nearestCity(NamedNodeMap nnm) {
		int x = Integer.parseInt(nnm.getNamedItem("x").getNodeValue());
		int y = Integer.parseInt(nnm.getNamedItem("y").getNodeValue());

		if (!spatialMap.isEmpty())
			XmlResultsPrinter.nearestCitySuccess(results, spatialMap, x, y);
		else
			XmlResultsPrinter.nearestCityError(results, "mapIsEmpty", x, y);
	}

	/**
	 * Prints the AVL tree.
	 */
	private static void printAvlTree() {
		if (!cityTree.isEmpty())
			XmlResultsPrinter.printAvlTreeSuccess(results, cityTree);
		else
			XmlResultsPrinter.printAvlTreeError(results, "emptyTree");
	}

	public static void main(String[] args) {
		Element r;


		stringCityMap = new TreeMap<String, City>(new AsciiComparator());
		citySet = new TreeSet<City>(new CoordinateComparator<City>());
		cityTree = new AvlTree(new CityNameComparator());

		try {
			results = XmlUtility.getDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e1) {}

		try {
			r = results.createElement("results");
			results.appendChild(r);

			Document doc = XmlUtility.validateNoNamespace(System.in);

//						Document doc = XmlUtility.validateNoNamespace(new File("/Users/jrtaylor/Documents/CMSC420Tests/avl1.input.xml"));

			Element ele = doc.getDocumentElement();
			spatialMap = new PRQuadtree<City>(0, Integer.parseInt(ele.getAttribute("spatialWidth")), 0, Integer.parseInt(ele.getAttribute("spatialHeight")));

			NodeList nl = ele.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node command = nl.item(i);
				if (command instanceof Element) {
					NamedNodeMap nnm = command.getAttributes();
					switch (command.getNodeName()) {
					case "createCity":
						createCity(nnm);
						break;
					case "deleteCity":
						deleteCity(nnm);
						break;
					case "clearAll":
						clearAll();
						break;
					case "listCities":
						listCities(nnm);
						break;
					case "mapCity":
						mapCity(nnm);
						break;
					case "unmapCity":
						unmapCity(nnm);
						break;
					case "printPRQuadtree":
						printPRQuadtree();
						break;
					case "saveMap":
						saveMap(nnm);
						break;
					case "rangeCities":
						rangeCities(nnm);
						break;
					case "nearestCity":
						nearestCity(nnm);
						break;
					case "printAvlTree":
						printAvlTree();
						break;
					default:
						XmlResultsPrinter.printFatalError(results);
					}
				}
			}
			//		} catch (IOException e) {
			//			XmlResultsPrinter.printUndefinedError(results);
		} catch (Exception e) {
			XmlResultsPrinter.printFatalError(results);
			e.printStackTrace();
		}

//		try {
//			XmlUtility.write(results, new File("/Users/jrtaylor/Documents/CMSC420Tests/output.xml"));
//		} catch (FileNotFoundException | TransformerException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		try {
			XmlUtility.print(results);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
