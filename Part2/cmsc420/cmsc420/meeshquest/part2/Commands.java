package cmsc420.meeshquest.part2;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cmsc420.PMQuadtree.*;
import cmsc420.Structure.*;
import cmsc420.drawing.CanvasPlus;
import cmsc420.geom.Geometry2D;
import cmsc420.sortedmap.AvlGTree;
import cmsc420.xml.XmlUtility;

public class Commands {
	boolean mapChanged = false;
	CanvasPlus map;
	Document results;
	PMQuadtree spatialMap;
	AvlGTree<String, Geometry2D> stringCityMap;
	TreeMap<String, Geometry2D> stringCityMapBackup;
	AvlGTree<String, City> mappedCities;
	TreeSet<Geometry2D> citySet;

	public Commands(Document results, int g, int pmOrder, int spatialWidth, int spatialHeight) {
		this.results = results;

		if (pmOrder == 1)
			spatialMap = new PM1Quadtree(spatialWidth, spatialHeight);
		else if (pmOrder == 3)
			spatialMap = new PM3Quadtree(spatialWidth, spatialHeight);

		stringCityMap = new AvlGTree<String, Geometry2D>(new AsciiComparator(), g);
		stringCityMapBackup = new TreeMap<String, Geometry2D>(new AsciiComparator());
		mappedCities = new AvlGTree<String, City>(new AsciiComparator(), g);
		citySet = new TreeSet<Geometry2D>(new CoordinateComparator());
		printToCanvas();
	}

	public int parseIntegerParameter(String name, Element element, Element parameters) {
		if (!element.hasAttribute(name))
			return -1;

		Element parameter = parameters.getOwnerDocument().createElement(name);

		String i = element.getAttribute(name);
		parameter.setAttribute("value", i);
		parameters.appendChild(parameter);
		return Integer.parseInt(i);
	}

	public String parseStringParameter(String name, Element element, Element parameters) {
		if (!element.hasAttribute(name))
			return null;

		Element parameter = parameters.getOwnerDocument().createElement(name);

		String str = element.getAttribute(name);
		parameter.setAttribute("value", str);
		parameters.appendChild(parameter);

		return str;
	}

	public Element parseCommandNode(Element element) {
		Element commandNode = results.createElement("command");

		if (element.hasAttribute("id"))
			commandNode.setAttribute("id", element.getAttribute("id"));
		commandNode.setAttribute("name", element.getTagName());

		return commandNode;
	}

	public void printError(String type, Element commandNode, Element parameterNode) {
		Element error = results.createElement("error");
		error.setAttribute("type", type);

		error.appendChild(commandNode);
		error.appendChild(parameterNode);

		results.getFirstChild().appendChild(error);
	}

	public void printSuccess(Element commandNode, Element parameterNode, Element outputNode) {
		Element success = results.createElement("success");

		success.appendChild(commandNode);
		success.appendChild(parameterNode);
		success.appendChild(outputNode);

		results.getFirstChild().appendChild(success);
	}

	public CanvasPlus printToCanvas() {
		if (!mapChanged && map != null)
			return map;

		int xMax = spatialMap.getMaxX();
		int yMax = spatialMap.getMaxY();

		map = new CanvasPlus("MeeshQuest", xMax, yMax);
		map.addRectangle(0, 0, xMax, yMax, Color.BLACK, false);

		printToCanvas(spatialMap.getRoot(), map);

		mapChanged = false;

		return map;
	}

	public void printToCanvas(String name) throws IOException {
		printToCanvas();


		map.save(name);
		map.dispose();
	}

	private void printToCanvas(PMQuadtreeNode node, CanvasPlus map) {
		City city = null;
		Road road = null;
		if (node.getType() == PMQuadtreeNode.BLACK) {
			for (Geometry2D g : ((PMQuadtreeBlack) node).getObjects()) {
				if (g.getType() == Geometry2D.POINT) {
					city = (City) g;
					map.addPoint(city.getName(), city.getX(), city.getY(), Color.BLACK);
				} else if (g.getType() == Geometry2D.SEGMENT) {
					road = (Road) g;
					map.addLine(road.getX1(), road.getY1(), road.getX2(), road.getY2(), Color.BLACK);
				}
			}
		} else if (node.getType() == PMQuadtreeNode.GRAY) {

			map.addCross(((PMQuadtreeGray)node).getRegion(0).getMaxX(), ((PMQuadtreeGray)node).getRegion(0).getMinY(), ((PMQuadtreeGray)node).getRegion(0).getWidth(), Color.GRAY);

			for (int i = 0; i < 4; i++) {
				printToCanvas(((PMQuadtreeGray) node).children[i], map);
			}
		}
	}
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
	public String createCity(Element element, Element parameterNode) {
		String result = "success";
		String name = parseStringParameter("name", element, parameterNode);
		int x = parseIntegerParameter("x", element, parameterNode);
		int y = parseIntegerParameter("y", element, parameterNode);
		int radius = parseIntegerParameter("radius", element, parameterNode);
		String color = parseStringParameter("color", element, parameterNode);

		City c = new City(name, x, y, radius, color);

		if (citySet.contains(c))
			result =  "duplicateCityCoordinates";
		else if (stringCityMap.containsKey(name))
			result =  "duplicateCityName";
		else {
			stringCityMapBackup.put(name, c);
			stringCityMap.put(name, c);
			citySet.add(c);
		}

		return result;
	}

	/**
	 * Resets all of the structures
	 */
	public String clearAll(Element element) {
		String result = "success";
		stringCityMap.clear();
		stringCityMapBackup.clear();
		spatialMap.clear();
		mappedCities.clear();
		citySet.clear();
		map = null;
		mapChanged = true;

		return result;
	}

	/**
	 * Prints all cities in the dictionary. Order is determined by sortBy parameter. Coordinate ordering is done 
	 * by Y coordinate first
	 * @param sortBy
	 * 		The order to sort the cities. Valid orders are "name" and "coordinate"
	 */
	public String listCities(Element element, Element parameterNode, Element outputNode) {
		String result = "success";
		String sortBy = parseStringParameter("sortBy", element, parameterNode);

		if (stringCityMap.isEmpty() || citySet.isEmpty())
			result = "noCitiesToList";
		else {
			Collection<Geometry2D> cityCollection = null;
			Element cityListNode = results.createElement("cityList");

			if (sortBy.equals("name"))
				cityCollection = stringCityMapBackup.values();
			else
				cityCollection = citySet;

			for (Geometry2D city : cityCollection) {
				((City) city).toXml(cityListNode);
			}

			outputNode.appendChild(cityListNode);
		}

		return result;
	}

	/**
	 * Inserts the city into the spatial map
	 * @param name
	 * 		The name of the city to map
	 */
	public String mapCity(Element element, Element parameterNode) {
		String result = "success";
		String name = parseStringParameter("name", element, parameterNode);

		City c = (City) stringCityMap.get(name);

		if (!stringCityMap.containsKey(name))
			result = "nameNotInDictionary";
		else if (spatialMap.find(c))
			result = "cityAlreadyMapped";
		else if (!spatialMap.isInBounds(c))
			result = "cityOutOfBounds";
		else {
			spatialMap.insert(c);
			mappedCities.put(name, c);
			c.setIsolated(true);
			mapChanged = true;
		}

		return result;
	}

	public String mapRoad(Element element, Element parameterNode, Element outputNode) {
		String result = "success";
		String start = parseStringParameter("start", element, parameterNode);
		String end = parseStringParameter("end", element, parameterNode);

		if (!stringCityMap.containsKey(start))
			result = "startPointDoesNotExist";
		else if (!stringCityMap.containsKey(end))
			result = "endPointDoesNotExist";
		else if (start.equals(end))
			result = "startEqualsEnd";
		else if (mappedCities.containsKey(start) || mappedCities.containsKey(end))
			result = "startOrEndIsIsolated";
		else {
			Geometry2D startCity = stringCityMap.get(start);
			Geometry2D endCity = stringCityMap.get(end);
			Road r = new Road((City) stringCityMap.get(start), (City) stringCityMap.get(end));
			if (spatialMap.find(r))
				result = "roadAlreadyMapped";
			else if (!spatialMap.isInBounds(r))
				result = "roadOutOfBounds";
			else {
				Element roadCreatedNode = results.createElement("roadCreated");
				roadCreatedNode.setAttribute("start", start);
				roadCreatedNode.setAttribute("end", end);

				if (spatialMap.isInBounds(startCity))
					spatialMap.insert(stringCityMap.get(start));
				if (spatialMap.isInBounds(endCity))
					spatialMap.insert(stringCityMap.get(end));

				spatialMap.insert(r);

				mapChanged = true;
				outputNode.appendChild(roadCreatedNode);
			}
		}
		return result;
	}

	/**
	 * Prints the PR Quadtree
	 */
	public String printPMQuadtree(Element element, Element outputNode) {
		String result = "success";

		if (spatialMap.isEmpty())
			result = "mapIsEmpty";
		else
			spatialMap.toXml(outputNode);

		return result;
	}

	/**
	 * Saves the map to a file
	 * @param name
	 * 		The name of the file
	 */
	public String saveMap(Element element, Element parameterNode) throws IOException {
		String result = "success";
		String name = parseStringParameter("name", element, parameterNode);
		printToCanvas(name);
		return result;
	}

	/**
	 * Lists the cities within the radius of coordinates (x,y) in the spatial map. Cities on the boundary, and at (x,y) are
	 * included.
	 * @param x
	 * @param y
	 * @param radius
	 */
	public String rangeCities(Element element, Element parameterNode, Element outputNode) throws IOException {
		String result = "success";
		int x = parseIntegerParameter("x", element, parameterNode);
		int y = parseIntegerParameter("y", element, parameterNode);
		int radius = parseIntegerParameter("radius", element, parameterNode);
		String saveMap = parseStringParameter("saveMap", element, parameterNode);

		PriorityQueue<Geometry2D> inRange;

		inRange = spatialMap.elementsInRange(x, y, radius, new CityNameComparator());

		if (inRange.isEmpty() || inRange.peek().getType() != Geometry2D.POINT) 
			result = "noCitiesExistInRange";
		else {
			Element cityListElement = results.createElement("cityList");

			if (saveMap != null) {
				map.addCircle(x, y, radius, Color.BLUE, false);
				printToCanvas(saveMap);
				map.removeCircle(x, y, radius, Color.BLUE, false);
			}

			Geometry2D g;
			do {
				g = inRange.remove();
				if (g.getType() == Geometry2D.POINT)
					((City) g).toXml(cityListElement);
			} while (!inRange.isEmpty() && g.getType() == Geometry2D.POINT);

			outputNode.appendChild(cityListElement);
		}

		return result;
	}

	public String rangeRoads(Element element, Element parameterNode, Element outputNode) throws IOException {
		String result = "success";
		int x = parseIntegerParameter("x", element, parameterNode);
		int y = parseIntegerParameter("y", element, parameterNode);
		int radius = parseIntegerParameter("radius", element, parameterNode);
		String saveMap = parseStringParameter("saveMap", element, parameterNode);

		PriorityQueue<Geometry2D> inRange;

		inRange = spatialMap.elementsInRange(x, y, radius, new RoadNameComparator());

		if (inRange.isEmpty() || inRange.peek().getType() != Geometry2D.SEGMENT) 
			result = "noRoadsExistInRange";
		else {
			Element roadListElement = results.createElement("roadList");

			if (saveMap != null) {
				map.addCircle(x, y, radius, Color.BLUE, false);
				printToCanvas(saveMap);
				map.removeCircle(x, y, radius, Color.BLUE, false);
			}

			Geometry2D g;
			do {
				g = inRange.remove();
				if (g.getType() == Geometry2D.SEGMENT)
					((Road) g).toXml(roadListElement);
			} while (!inRange.isEmpty() && g.getType() == Geometry2D.SEGMENT);

			outputNode.appendChild(roadListElement);
		}

		return result;
	}

	/**
	 * Returns the name and location of the nearest city to points (x,y) on the spatial map.
	 * @param x
	 * @param y
	 */
	public String nearestCity(Element element, Element parameterNode, Element outputNode) {
		String result = "success";

		int x = parseIntegerParameter("x", element, parameterNode);
		int y = parseIntegerParameter("y", element, parameterNode);

		Geometry2D nearest = (City) spatialMap.nearestElement(new CityDistanceComparator(x, y));

		if (nearest == null || nearest.getType() != Geometry2D.POINT || ((City) nearest).isIsolated())
			result = "cityNotFound";
		else
			((City) nearest).toXml(outputNode);

		return result;
	}

	public String nearestIsolatedCity(Element element, Element parameterNode, Element outputNode) {
		String result = "success";
		int x = parseIntegerParameter("x", element, parameterNode);
		int y = parseIntegerParameter("y", element, parameterNode);

		Geometry2D nearest = spatialMap.nearestElement(new IsolatedCityDistanceComparator(x, y));

		if (nearest == null || nearest.getType() != Geometry2D.POINT || !((City) nearest).isIsolated())
			result = "cityNotFound";
		else {
			Element isolatedCityNode = results.createElement("isolatedCity");
			isolatedCityNode.setAttribute("name", ((City) nearest).getName());
			isolatedCityNode.setAttribute("color", ((City) nearest).getColor());
			isolatedCityNode.setAttribute("x", Integer.toString((int) ((City) nearest).getX()));
			isolatedCityNode.setAttribute("y", Integer.toString((int) ((City) nearest).getY()));
			isolatedCityNode.setAttribute("radius", Integer.toString((int) ((City) nearest).getRadius()));

			outputNode.appendChild(isolatedCityNode);
		}

		return result;
	}

	public String nearestRoad(Element element, Element parameterNode, Element outputNode) {
		String result = "success";
		int x = parseIntegerParameter("x", element, parameterNode);
		int y = parseIntegerParameter("y", element, parameterNode);

		Geometry2D nearest = spatialMap.nearestElement(new RoadCityDistanceComparator(x, y));

		if (nearest == null || nearest.getType() != Geometry2D.SEGMENT)
			result = "roadNotFound";
		else
			((Road) nearest).toXml(outputNode);

		return result;
	}

	public String nearestCityToRoad(Element element, Element parameterNode, Element outputNode) {
		String result = "success";
		String start = parseStringParameter("start", element, parameterNode);
		String end = parseStringParameter("end", element, parameterNode);

		if (!stringCityMap.containsKey(start) || !stringCityMap.containsKey(end))
			result = "roadIsNotMapped";
		else {
			Geometry2D startCity = stringCityMap.get(start);
			Geometry2D endCity = stringCityMap.get(end);
			Geometry2D road = new Road((City) startCity, (City) endCity);

			if (!spatialMap.getEdgeSet().contains(road))
				result = "roadIsNotMapped";
			else if (spatialMap.getPointSet().size() <= 2)
				result = "noOtherCitiesMapped";
			else {
				Geometry2D nearest = spatialMap.nearestElement(new CityRoadDistanceComparator((Road) road));
				((City) nearest).toXml(outputNode);
			}
		}
		return result;
	}

	public String shortestPath(Element element, Element commandNode, Element parameterNode, Element outputNode) throws IOException, ParserConfigurationException, TransformerException {
		String result = "success";
		String start = parseStringParameter("start", element, parameterNode);
		String end = parseStringParameter("end", element, parameterNode);
		String saveMap = parseStringParameter("saveMap", element, parameterNode);
		String saveHTML = parseStringParameter("saveHTML", element, parameterNode);

		City startCity = (City) stringCityMap.get(start);
		City endCity = (City) stringCityMap.get(end);

		if (startCity == null || !spatialMap.getPointSet().contains(startCity))
			result = "nonExistentStart";
		else if (endCity == null || !spatialMap.getPointSet().contains(endCity))
			result = "nonExistentEnd";
		else {
			AdjacencyList adjList = new AdjacencyList();
			TreeMap<String, Double> distance = new TreeMap<String, Double>();
			TreeMap<String, Geometry2D> previous = new TreeMap<String, Geometry2D>();
			PriorityQueue<GeometryDistanceCouple> queue = new PriorityQueue<GeometryDistanceCouple>();

			distance.put(start, 0.0);

			Road edgeRoad;

			for (Geometry2D edge : spatialMap.getEdgeSet()) {
				edgeRoad = (Road) edge;
				if (!edgeRoad.start.equals(startCity))
					distance.put(edgeRoad.start.getName(), Double.MAX_VALUE);

				if (!edgeRoad.end.equals(startCity))
					distance.put(edgeRoad.end.getName(), Double.MAX_VALUE);

				adjList.add(edgeRoad.end.getName(), edgeRoad.start);
				adjList.add(edgeRoad.start.getName(), edgeRoad.end);

				queue.add(new GeometryDistanceCouple(edgeRoad.start, distance.get(edgeRoad.start.getName())));
				queue.add(new GeometryDistanceCouple(edgeRoad.end, distance.get(edgeRoad.end.getName())));
			}

			City uCity, vCity;
			while (!queue.isEmpty()) {
				GeometryDistanceCouple u = queue.remove();

				uCity = (City) u.getGeometry();
				Iterator<Geometry2D> iter = adjList.get(uCity.getName()).iterator();

				do {
					vCity = (City) iter.next();
					double altDist = distance.get(uCity.getName()) + vCity.distance(uCity);

					if (altDist < distance.get(vCity.getName())) {
						distance.put(vCity.getName(), altDist);
						previous.put(vCity.getName(), uCity);
						GeometryDistanceCouple vCouple = new GeometryDistanceCouple((Geometry2D) vCity, altDist);
						queue.remove(vCouple);
						queue.add(vCouple);
					}
				} while (iter.hasNext());
			}



			Element path = results.createElement("path");
			path.setAttribute("length", String.format("%.3f", distance.get(end)));
			City prevCity = endCity;
			City currCity = (City) previous.get(prevCity.getName());
			City nextCity = null;

			Element roadNode;
			int hops = 0;

			CanvasPlus canvas = null;

			if (saveMap != null || saveHTML != null) {
				canvas = new CanvasPlus("MeeshQuest", spatialMap.getMaxX(), spatialMap.getMaxY());
				canvas.addRectangle(0, 0, spatialMap.getMaxX(), spatialMap.getMaxY(), Color.BLACK, false);
				canvas.addPoint(start, startCity.getX(), startCity.getY(), Color.GREEN);
				canvas.addPoint(end, endCity.getX(), endCity.getY(), Color.RED);
			}

			while (currCity != null) {
				nextCity = (City) previous.get(currCity.getName());

				roadNode = results.createElement("road");
				roadNode.setAttribute("start", currCity.getName());
				roadNode.setAttribute("end", prevCity.getName());
				path.insertBefore(roadNode, path.getFirstChild());
				if (nextCity != null)
					path.insertBefore(getDirection(prevCity, currCity, nextCity), path.getFirstChild());
				if (saveMap != null || saveHTML != null) {
					canvas.addPoint(currCity.getName(), currCity.getX(), currCity.getY(), Color.BLUE);
					canvas.addLine(prevCity.getX(), prevCity.getY(), currCity.getX(), currCity.getY(), Color.BLUE);
				}

				prevCity = currCity;
				currCity = (City) previous.get(prevCity.getName());
				hops++;
			}

			if (!prevCity.equals(startCity))
				result = "noPathExists";
			else {
				path.setAttribute("hops", Integer.toString(hops));

				outputNode.appendChild(path);

				if (saveMap != null) {
					canvas.save(saveMap);
				}
				if (saveHTML != null) {
					canvas.save(saveHTML);
					Document shortestPathDoc = XmlUtility.getDocumentBuilder().newDocument();
					Element successNode = shortestPathDoc.createElement("success");
					Element spCommandNode = (Element) shortestPathDoc.importNode(commandNode, true);
					Element spParameterNode = (Element) shortestPathDoc.importNode(parameterNode, true);
					Element spOutputNode = (Element) shortestPathDoc.importNode(outputNode, true);
					successNode.appendChild(spCommandNode);
					successNode.appendChild(spParameterNode);
					successNode.appendChild(spOutputNode);
					shortestPathDoc.appendChild(successNode);
					XmlUtility.transform(shortestPathDoc, new File("shortestPath.xsl"), new File(saveHTML + ".html"));
				}

				if (canvas != null)
					canvas.dispose();
			}
		}
		return result;
	}

	private Element getDirection(City start, City middle, City end) {
		Arc2D.Double arc = new Arc2D.Double();

		arc.setArcByTangent(start, middle, end, 5);
		double angle = arc.getAngleExtent() > 0 ? arc.getAngleExtent() - 180 : arc.getAngleExtent() + 180;

		if (angle > 0 && angle <= 135)
			return results.createElement("right");
		else if (angle > 135 && angle <= 180 || angle < -135 && angle >= -180)
			return results.createElement("straight");
		else if (angle < 0 && angle >= -135)
			return results.createElement("left");
		else 
			return null;
	}

	/**
	 * Prints the AVL tree.
	 */
	public String printAvlTree(Element element, Element outputNode) {
		String result = "success";

		if (stringCityMap.isEmpty())
			result = "emptyTree";
		else
			stringCityMap.toXml(outputNode);
		return result;
	}

	public void parseCommands(NodeList commandList) throws IOException, ParserConfigurationException, TransformerException {
		Element commandNode = null;
		Element parameterNode = null;
		Element outputNode = null;
		String result = null;

		for (int i = 0; i < commandList.getLength(); i++) {
			if (commandList.item(i) instanceof Element) {
				Element commandElement = (Element) commandList.item(i);

				commandNode = parseCommandNode(commandElement);
				parameterNode = results.createElement("parameters");
				outputNode = results.createElement("output");

				switch (commandElement.getTagName()) {
				case "createCity":
					result = createCity(commandElement, parameterNode);
					break;
				case "clearAll":
					result = clearAll(commandElement);
					break;
				case "listCities":
					result = listCities(commandElement, parameterNode, outputNode);
					break;
				case "mapRoad":
					result = mapRoad(commandElement, parameterNode, outputNode);
					break;
				case "mapCity":
					result = mapCity(commandElement, parameterNode);
					break;
				case "printPMQuadtree":
					result = printPMQuadtree(commandElement, outputNode);
					break;
				case "saveMap":
					result = saveMap(commandElement, parameterNode);
					break;
				case "rangeCities":
					result = rangeCities(commandElement, parameterNode, outputNode);
					break;
				case "rangeRoads":
					result = rangeRoads(commandElement, parameterNode, outputNode);
					break;
				case "nearestCity":
					result = nearestCity(commandElement, parameterNode, outputNode);
					break;
				case "nearestIsolatedCity":
					result = nearestIsolatedCity(commandElement, parameterNode, outputNode);
					break;
				case "nearestRoad":
					result = nearestRoad(commandElement, parameterNode, outputNode);
					break;
				case "nearestCityToRoad":
					result = nearestCityToRoad(commandElement, parameterNode, outputNode);
					break;
				case "shortestPath":
					result = shortestPath(commandElement, commandNode, parameterNode, outputNode);
					break;
				case "printAvlTree":
					result = printAvlTree(commandElement, outputNode);
					break;
				}

				if (!result.equals("success"))
					printError(result, commandNode, parameterNode);
				else
					printSuccess(commandNode, parameterNode, outputNode);
			}
		}
	}
}

class GeometryDistanceCouple implements Comparable<GeometryDistanceCouple> {

	Geometry2D geometry;
	Double distance;

	public GeometryDistanceCouple(Geometry2D g, Double distance) {
		this.geometry = g;
		this.distance = distance;
	}

	@Override
	public int compareTo(GeometryDistanceCouple o) {
		return distance.compareTo(o.distance);
	}

	public void setDistance(Double newDistance) {
		this.distance = newDistance;
	}

	public Geometry2D getGeometry() {
		return geometry;
	}
}
