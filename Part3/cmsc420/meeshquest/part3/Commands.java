package cmsc420.meeshquest.part3;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cmsc420.MinimumSpanningTree;
import cmsc420.SpatialMapLine;
import cmsc420.drawing.CanvasPlus;
import cmsc420.exceptions.MyException;
import cmsc420.geom.Geometry2D;
import cmsc420.sortedmap.AvlGTree;
import cmsc420.sortedmap.AvlNode;
import cmsc420.structure.Airport;
import cmsc420.structure.City;
import cmsc420.structure.Road;
import cmsc420.structure.SpatialMap;
import cmsc420.structure.SpatialMapPoint;
import cmsc420.structure.Terminal;
import cmsc420.structure.PMQuadtree.PMQuadtree;
import cmsc420.structure.PMQuadtree.PMQuadtreeBlack;
import cmsc420.structure.PMQuadtree.PMQuadtreeGray;
import cmsc420.structure.PMQuadtree.PMQuadtreeNode;
import cmsc420.structure.comparators.GeometryNameComparator;

public class Commands {
	//The Document that the commands are going to print to
	Document results;

	//The SpatialMap the the commands are going to be manipulating
	SpatialMap spatialMap;

	public Commands(Document results, SpatialMap spatialMap) {
		this.results = results;

		this.spatialMap = spatialMap;
	}

	private static int parseIntegerParameter(String name, Element sourceNode, Element parameterNode) {
		if (!sourceNode.hasAttribute(name))
			return -1;

		Element parameter = parameterNode.getOwnerDocument().createElement(name);

		String i = sourceNode.getAttribute(name);
		parameter.setAttribute("value", i);
		parameterNode.appendChild(parameter);
		return Integer.parseInt(i);
	}

	private static String parseStringParameter(String name, Element sourceNode, Element parameterNode) {
		if (!sourceNode.hasAttribute(name))
			return null;

		Element parameter = parameterNode.getOwnerDocument().createElement(name);

		String str = sourceNode.getAttribute(name);
		parameter.setAttribute("value", str);
		parameterNode.appendChild(parameter);

		return str;
	}

	private Element parseCommandNode(Element sourceNode) {
		Element commandNode = results.createElement("command");

		if (sourceNode.hasAttribute("id"))
			commandNode.setAttribute("id", sourceNode.getAttribute("id"));
		commandNode.setAttribute("name", sourceNode.getTagName());

		return commandNode;
	}

	private void printError(String type, Element commandNode, Element parameterNode) {
		Element error = results.createElement("error");
		error.setAttribute("type", type);

		error.appendChild(commandNode);
		error.appendChild(parameterNode);

		results.getFirstChild().appendChild(error);
	}

	private static void printSpatialMapPointXML(SpatialMapPoint point, Element parent) {
		switch(point.getStucture()) {
		case SpatialMapPoint.CITY:
			printSpatialMapPointXML(point, "city", parent);
			break;
		case SpatialMapPoint.TERMINAL:
			printSpatialMapPointXML(point, "terminal", parent);
			break;
		case SpatialMapPoint.AIRPORT:
			printSpatialMapPointXML(point, "airport", parent);
			break;
		}
	}

	private static void printSpatialMapPointXML(SpatialMapPoint point, String name, Element parent) {
		Element pointNode = parent.getOwnerDocument().createElement(name);
		switch(point.getStucture()) {
		case SpatialMapPoint.CITY:
			pointNode.setAttribute("color", ((City) point).getColor());
			pointNode.setAttribute("radius", Integer.toString((int) ((City) point).getRadius()));
			break;
		case SpatialMapPoint.TERMINAL:
			pointNode.setAttribute("airportName", ((Terminal) point).getAirport().getName());
			pointNode.setAttribute("cityName", ((Terminal) point).getTerminalCity().getName());
			break;
		case SpatialMapPoint.AIRPORT:
			break;
		default:
			throw new IllegalArgumentException();
		}

		pointNode.setAttribute("name", point.getName());
		pointNode.setAttribute("localX", Integer.toString((int) point.getX()));
		pointNode.setAttribute("localY", Integer.toString((int) point.getY()));
		pointNode.setAttribute("remoteX", Integer.toString((int) point.getRemoteX()));
		pointNode.setAttribute("remoteY", Integer.toString((int) point.getRemoteY()));
		parent.appendChild(pointNode);
	}

	private static void printRoadXml(Road road, String name, Element parent) {
		Element roadNode = parent.getOwnerDocument().createElement(name);
		roadNode.setAttribute("start", road.getSMP1().getName());
		roadNode.setAttribute("end", road.getSMP2().getName());
		parent.appendChild(roadNode);
	}

	private void printSuccess(Element commandNode, Element parameterNode, Element outputNode) {
		Element success = results.createElement("success");

		success.appendChild(commandNode);
		success.appendChild(parameterNode);
		success.appendChild(outputNode);

		results.getFirstChild().appendChild(success);
	}

	private void createCity(Element sourceNode, Element parameterNode) throws MyException {
		String name = parseStringParameter("name", sourceNode, parameterNode);
		int localX = parseIntegerParameter("localX", sourceNode, parameterNode);
		int localY = parseIntegerParameter("localY", sourceNode, parameterNode);
		int remoteX = parseIntegerParameter("remoteX", sourceNode, parameterNode);
		int remoteY = parseIntegerParameter("remoteY", sourceNode, parameterNode);
		int radius = parseIntegerParameter("radius", sourceNode, parameterNode);
		String color = parseStringParameter("color", sourceNode, parameterNode);

		spatialMap.createCity(name, localX, localY, remoteX, remoteY, radius, color);
	}

	private void clearAll() {
		spatialMap.clear();
	}

	private void deleteCity(Element sourceNode, Element parameterNode, Element outputNode) throws MyException {
		String name = parseStringParameter("name", sourceNode, parameterNode);

		ArrayList<Geometry2D> removedGeometry = spatialMap.deleteCity(name);

		for (Geometry2D g : removedGeometry) {
			if (g.getType() == Geometry2D.POINT)
				printSpatialMapPointXML((City) g,"cityUnmapped", outputNode); 
			else
				printRoadXml((Road) g, "roadUnmapped", outputNode);
		}
	}

	private void listCities(Element element, Element parameterNode, Element outputNode) throws MyException {
		String sortBy = parseStringParameter("sortBy", element, parameterNode);

		Collection<City> cityCollection = spatialMap.listCities(sortBy);
		Element cityListNode = results.createElement("cityList");

		for (City city : cityCollection) {
			printSpatialMapPointXML(city, "city", cityListNode);
		}

		outputNode.appendChild(cityListNode);
	}

	private void mapAirport(Element element, Element parameterNode) throws MyException {
		String name = parseStringParameter("name", element, parameterNode);
		int localX = parseIntegerParameter("localX", element, parameterNode);
		int localY = parseIntegerParameter("localY", element, parameterNode);
		int remoteX = parseIntegerParameter("remoteX", element, parameterNode);
		int remoteY = parseIntegerParameter("remoteY", element, parameterNode);
		String terminalName = parseStringParameter("terminalName", element, parameterNode);
		int terminalX = parseIntegerParameter("terminalX", element, parameterNode);
		int terminalY = parseIntegerParameter("terminalY", element, parameterNode);
		String terminalCity = parseStringParameter("terminalCity", element, parameterNode);

		spatialMap.mapAirport(name, localX, localY, remoteX, remoteY, terminalName, terminalX, terminalY, terminalCity);
	}

	private void mapTerminal(Element sourceNode, Element parameterNode) throws MyException {
		String name = parseStringParameter("name", sourceNode, parameterNode);
		int localX = parseIntegerParameter("localX", sourceNode, parameterNode);
		int localY = parseIntegerParameter("localY", sourceNode, parameterNode);
		int remoteX = parseIntegerParameter("remoteX", sourceNode, parameterNode);
		int remoteY = parseIntegerParameter("remoteY", sourceNode, parameterNode);
		String cityName = parseStringParameter("cityName", sourceNode, parameterNode);
		String airportName = parseStringParameter("airportName", sourceNode, parameterNode);

		spatialMap.mapTerminal(name, localX, localY, remoteX, remoteY, airportName, cityName);
	}

	private void mapRoad(Element element, Element parameterNode, Element outputNode) throws MyException {
		String start = parseStringParameter("start", element, parameterNode);
		String end = parseStringParameter("end", element, parameterNode);

		spatialMap.mapRoad(start, end);

		Element roadCreatedNode = results.createElement("roadCreated");
		roadCreatedNode.setAttribute("start", start);
		roadCreatedNode.setAttribute("end", end);

		outputNode.appendChild(roadCreatedNode);
	}

	private void unmapRoad(Element sourceNode, Element parameterNode, Element outputNode) throws MyException {
		String start = parseStringParameter("start", sourceNode, parameterNode);
		String end = parseStringParameter("end", sourceNode, parameterNode);

		spatialMap.unmapRoad(start, end);

		Element roadDeletedNode = results.createElement("roadDeleted");
		roadDeletedNode.setAttribute("start", start);
		roadDeletedNode.setAttribute("end", end);
		outputNode.appendChild(roadDeletedNode);
	}

	private void unmapAirport(Element sourceNode, Element parameterNode, Element outputNode) throws MyException {
		String name = parseStringParameter("name", sourceNode, parameterNode);

		ArrayList<Terminal> removedTerminals = spatialMap.unmapAirport(name);

		Element terminalNode;
		for (Terminal terminal : removedTerminals) {
			terminalNode = results.createElement("terminalUnmapped");
			terminalNode.setAttribute("name", terminal.getName());
			outputNode.appendChild(terminalNode);
		}
	}

	private void unmapTerminal(Element sourceNode, Element parameterNode, Element outputNode) throws MyException {
		String name = parseStringParameter("name", sourceNode, parameterNode);

		Airport removedAirport = spatialMap.unmapTerminal(name);

		if (removedAirport != null) {
			Element airportNode = results.createElement("airportUnmapped");
			airportNode.setAttribute("name", removedAirport.getName());
			outputNode.appendChild(airportNode);
		}
	}

	/**
	 * Prints the PR Quadtree
	 */
	private void printPMQuadtree(Element element, Element parameterNode, Element outputNode) throws MyException {
		int remoteX = parseIntegerParameter("remoteX", element, parameterNode);
		int remoteY = parseIntegerParameter("remoteY", element, parameterNode);

		PMQuadtree localMap = spatialMap.getMetropole(remoteX, remoteY);

		Element pmQuadtreeNode = results.createElement("quadtree");
		pmQuadtreeNode.setAttribute("order", Integer.toString(localMap.getOrder()));

		Element child = printPMQuadtreeHelper(localMap.getRoot());

		pmQuadtreeNode.appendChild(child);

		outputNode.appendChild(pmQuadtreeNode);
	}

	private Element printPMQuadtreeHelper(PMQuadtreeNode node) {
		Element nodeNode;

		if (node.getType() == PMQuadtreeNode.BLACK)
			nodeNode = printBlackNode((PMQuadtreeBlack) node);
		else if (node.getType() == PMQuadtreeNode.GRAY)
			nodeNode = printGrayNode((PMQuadtreeGray) node);
		else
			nodeNode = results.createElement("white");

		return nodeNode;
	}

	private Element printBlackNode(PMQuadtreeBlack node) {
		Element nodeNode = results.createElement("black");

		nodeNode.setAttribute("cardinality", Integer.toString(node.getCardinality()));
		if (node.getPoint() != null)
			printSpatialMapPointXML(node.getPoint(), nodeNode);
		ArrayList<SpatialMapLine> roads = node.getEdges();
		roads.sort(GeometryNameComparator.getInstance());
		for (SpatialMapLine road : roads) {
			printRoadXml((Road) road, "road", nodeNode);
		}

		return nodeNode;
	}

	private Element printGrayNode(PMQuadtreeGray node) {
		Element nodeNode = results.createElement("gray");

		Element childNode;

		nodeNode.setAttribute("x", Integer.toString((int) node.getRegion(0).getMaxX()));
		nodeNode.setAttribute("y", Integer.toString((int) node.getRegion(0).getMinY()));
		for (int i = 0; i < 4; i++) {
			childNode = printPMQuadtreeHelper(node.getChild(i));
			nodeNode.appendChild(childNode);
		}

		return nodeNode;
	}

	/**
	 * Saves the map to a file
	 * @param name
	 * 		The name of the file
	 * @throws MyException 
	 */
	private void saveMap(Element element, Element parameterNode) throws IOException, MyException {
		int remoteX = parseIntegerParameter("remoteX", element, parameterNode);
		int remoteY = parseIntegerParameter("remoteY", element, parameterNode);
		String name = parseStringParameter("name", element, parameterNode);

		CanvasPlus map = drawMap(remoteX, remoteY);
		map.save(name);
		map.dispose();
	}

	/**
	 * Lists the cities within the radius of coordinates (x,y) in the spatial map. Cities on the boundary, and at (x,y) are
	 * included.
	 * @param localX
	 * @param localY
	 * @param radius
	 * @throws MyException 
	 */
	private void globalRangeCities(Element element, Element parameterNode, Element outputNode) throws MyException {
		int remoteX = parseIntegerParameter("remoteX", element, parameterNode);
		int remoteY = parseIntegerParameter("remoteY", element, parameterNode);
		int radius = parseIntegerParameter("radius", element, parameterNode);

		ArrayList<City> inRange = spatialMap.globalRangeCities(remoteX, remoteY, radius);

		Element cityListElement = results.createElement("cityList");

		for (City city : inRange) {
			printSpatialMapPointXML(city, cityListElement);
		}

		outputNode.appendChild(cityListElement);
	}

	//	private String rangeRoads(Element element, Element parameterNode, Element outputNode) throws IOException {
	//		int x = parseIntegerParameter("x", element, parameterNode);
	//		int y = parseIntegerParameter("y", element, parameterNode);
	//		int radius = parseIntegerParameter("radius", element, parameterNode);
	//		String saveMap = parseStringParameter("saveMap", element, parameterNode);
	//
	//		PriorityQueue<Geometry2D> inRange;
	//
	//		inRange = spatialMap.elementsInRange(x, y, radius, new RoadNameComparator());
	//
	//		if (inRange.isEmpty() || inRange.peek().getType() != Geometry2D.SEGMENT) 
	//			return "noRoadsExistInRange";
	//		else {
	//			Element roadListElement = results.createElement("roadList");
	//
	//			if (saveMap != null) {
	//				map.addCircle(x, y, radius, Color.BLUE, false);
	//				printToCanvas(saveMap);
	//				map.removeCircle(x, y, radius, Color.BLUE, false);
	//			}
	//
	//			Geometry2D g;
	//			do {
	//				g = inRange.remove();
	//				if (g.getType() == Geometry2D.SEGMENT)
	//					((Road) g).toXml(roadListElement);
	//			} while (!inRange.isEmpty() && g.getType() == Geometry2D.SEGMENT);
	//
	//			outputNode.appendChild(roadListElement);
	//		}
	//
	//		return "success";
	//	}

	/**
	 * Returns the name and location of the nearest city to points (x,y) on the spatial map.
	 * @param localX
	 * @param localY
	 * @throws MyException 
	 */
	private void nearestCity(Element element, Element parameterNode, Element outputNode) throws MyException {
		int localX = parseIntegerParameter("localX", element, parameterNode);
		int localY = parseIntegerParameter("localY", element, parameterNode);
		int remoteX = parseIntegerParameter("remoteX", element, parameterNode);
		int remoteY = parseIntegerParameter("remoteY", element, parameterNode);

		City nearest = spatialMap.nearestCity(localX, localY, remoteX, remoteY);

		printSpatialMapPointXML(nearest, outputNode);
	}

	private void mst(Element sourceNode, Element parameterNode, Element outputNode) throws MyException {
		String start = parseStringParameter("start", sourceNode, parameterNode);

		MinimumSpanningTree mst = spatialMap.minimumSpanningTree(start);
		Element mstNode = results.createElement("mst");
		mstNode.setAttribute("distanceSpanned", String.format("%.3f", mst.getDistanceSpanned()));
		TreeMap<String, TreeSet<String>> nodes = mst.getNext();

		mstHelper(start, nodes, mstNode);
		outputNode.appendChild(mstNode);
	}

	private void mstHelper(String node, TreeMap<String, TreeSet<String>> nextNodes, Element parent) {
		Element currNode = parent.getOwnerDocument().createElement("node");
		currNode.setAttribute("name", node);
		if (nextNodes.containsKey(node)) {
			for (String next : nextNodes.get(node)) {
				mstHelper(next, nextNodes, currNode);
			}

			
		}
		parent.appendChild(currNode);
	}

	//	private String nearestIsolatedCity(Element element, Element parameterNode, Element outputNode) {
	//		int x = parseIntegerParameter("x", element, parameterNode);
	//		int y = parseIntegerParameter("y", element, parameterNode);
	//
	//		Geometry2D nearest = spatialMap.nearestElement(new IsolatedCityDistanceComparator(x, y));
	//
	//		if (nearest == null || nearest.getType() != Geometry2D.POINT || !((City) nearest).isIsolated())
	//			return"cityNotFound";
	//		else {
	//			Element isolatedCityNode = results.createElement("isolatedCity");
	//			isolatedCityNode.setAttribute("name", ((City) nearest).getName());
	//			isolatedCityNode.setAttribute("color", ((City) nearest).getColor());
	//			isolatedCityNode.setAttribute("x", Integer.toString((int) ((City) nearest).getX()));
	//			isolatedCityNode.setAttribute("y", Integer.toString((int) ((City) nearest).getY()));
	//			isolatedCityNode.setAttribute("radius", Integer.toString((int) ((City) nearest).getRadius()));
	//
	//			outputNode.appendChild(isolatedCityNode);
	//		}
	//
	//		return "success";
	//	}
	//
	//	private String nearestRoad(Element element, Element parameterNode, Element outputNode) {
	//		int x = parseIntegerParameter("x", element, parameterNode);
	//		int y = parseIntegerParameter("y", element, parameterNode);
	//
	//		Geometry2D nearest = spatialMap.nearestElement(new RoadCityDistanceComparator(x, y));
	//
	//		if (nearest == null || nearest.getType() != Geometry2D.SEGMENT)
	//			return "roadNotFound";
	//		else
	//			((Road) nearest).toXml(outputNode);
	//
	//		return "success";
	//	}

	//	private String nearestCityToRoad(Element element, Element parameterNode, Element outputNode) {
	//		String start = parseStringParameter("start", element, parameterNode);
	//		String end = parseStringParameter("end", element, parameterNode);
	//
	//		if (!cities.containsKey(start) || !cities.containsKey(end))
	//			return"roadIsNotMapped";
	//		else {
	//			Geometry2D startCity = cities.get(start);
	//			Geometry2D endCity = cities.get(end);
	//			Geometry2D road = new Road((City) startCity, (City) endCity);
	//
	//			if (!spatialMap.getEdgeSet().contains(road))
	//				return"roadIsNotMapped";
	//			else if (spatialMap.getPointSet().size() <= 2)
	//				return"noOtherCitiesMapped";
	//			else {
	//				Geometry2D nearest = spatialMap.nearestElement(new CityRoadDistanceComparator((Road) road));
	//				((City) nearest).toXml(outputNode);
	//			}
	//		}
	//		return "success";
	//	}

	//	private String shortestPath(Element element, Element commandNode, Element parameterNode, Element outputNode) throws IOException, ParserConfigurationException, TransformerException {
	//		String start = parseStringParameter("start", element, parameterNode);
	//		String end = parseStringParameter("end", element, parameterNode);
	//		String saveMap = parseStringParameter("saveMap", element, parameterNode);
	//		String saveHTML = parseStringParameter("saveHTML", element, parameterNode);
	//
	//		City startCity = (City) cities.get(start);
	//		City endCity = (City) cities.get(end);
	//
	//		if (startCity == null || !spatialMap.getPointSet().contains(startCity))
	//			return"nonExistentStart";
	//		else if (endCity == null || !spatialMap.getPointSet().contains(endCity))
	//			return"nonExistentEnd";
	//		else {
	//			AdjacencyList adjList = new AdjacencyList();
	//			TreeMap<String, Double> distance = new TreeMap<String, Double>();
	//			TreeMap<String, Geometry2D> previous = new TreeMap<String, Geometry2D>();
	//			PriorityQueue<GeometryDistanceCouple> queue = new PriorityQueue<GeometryDistanceCouple>();
	//
	//			distance.put(start, 0.0);
	//
	//			Road edgeRoad;
	//
	//			for (Geometry2D edge : spatialMap.getEdgeSet()) {
	//				edgeRoad = (Road) edge;
	//				if (!edgeRoad.start.equals(startCity))
	//					distance.put(edgeRoad.start.getName(), Double.MAX_VALUE);
	//
	//				if (!edgeRoad.end.equals(startCity))
	//					distance.put(edgeRoad.end.getName(), Double.MAX_VALUE);
	//
	//				adjList.add(edgeRoad.end.getName(), edgeRoad.start);
	//				adjList.add(edgeRoad.start.getName(), edgeRoad.end);
	//
	//				queue.add(new GeometryDistanceCouple(edgeRoad.start, distance.get(edgeRoad.start.getName())));
	//				queue.add(new GeometryDistanceCouple(edgeRoad.end, distance.get(edgeRoad.end.getName())));
	//			}
	//
	//			City uCity, vCity;
	//			while (!queue.isEmpty()) {
	//				GeometryDistanceCouple u = queue.remove();
	//
	//				uCity = (City) u.getGeometry();
	//				Iterator<Geometry2D> iter = adjList.get(uCity.getName()).iterator();
	//
	//				do {
	//					vCity = (City) iter.next();
	//					double altDist = distance.get(uCity.getName()) + vCity.distance(uCity);
	//
	//					if (altDist < distance.get(vCity.getName())) {
	//						distance.put(vCity.getName(), altDist);
	//						previous.put(vCity.getName(), uCity);
	//						GeometryDistanceCouple vCouple = new GeometryDistanceCouple((Geometry2D) vCity, altDist);
	//						queue.remove(vCouple);
	//						queue.add(vCouple);
	//					}
	//				} while (iter.hasNext());
	//			}
	//
	//			Element path = results.createElement("path");
	//			path.setAttribute("length", String.format("%.3f", distance.get(end)));
	//			City prevCity = endCity;
	//			City currCity = (City) previous.get(prevCity.getName());
	//			City nextCity = null;
	//
	//			Element roadNode;
	//			int hops = 0;
	//
	//			CanvasPlus canvas = null;
	//
	//			if (saveMap != null || saveHTML != null) {
	//				canvas = new CanvasPlus("MeeshQuest", spatialMap.getMaxX(), spatialMap.getMaxY());
	//				canvas.addRectangle(0, 0, spatialMap.getMaxX(), spatialMap.getMaxY(), Color.BLACK, false);
	//				canvas.addPoint(start, startCity.getX(), startCity.getY(), Color.GREEN);
	//				canvas.addPoint(end, endCity.getX(), endCity.getY(), Color.RED);
	//			}
	//
	//			while (currCity != null) {
	//				nextCity = (City) previous.get(currCity.getName());
	//
	//				roadNode = results.createElement("road");
	//				roadNode.setAttribute("start", currCity.getName());
	//				roadNode.setAttribute("end", prevCity.getName());
	//				path.insertBefore(roadNode, path.getFirstChild());
	//				if (nextCity != null)
	//					path.insertBefore(getDirection(prevCity, currCity, nextCity), path.getFirstChild());
	//				if (saveMap != null || saveHTML != null) {
	//					canvas.addPoint(currCity.getName(), currCity.getX(), currCity.getY(), Color.BLUE);
	//					canvas.addLine(prevCity.getX(), prevCity.getY(), currCity.getX(), currCity.getY(), Color.BLUE);
	//				}
	//
	//				prevCity = currCity;
	//				currCity = (City) previous.get(prevCity.getName());
	//				hops++;
	//			}
	//
	//			if (!prevCity.equals(startCity))
	//				return"noPathExists";
	//			else {
	//				path.setAttribute("hops", Integer.toString(hops));
	//
	//				outputNode.appendChild(path);
	//
	//				if (saveMap != null) {
	//					canvas.save(saveMap);
	//				}
	//				if (saveHTML != null) {
	//					canvas.save(saveHTML);
	//					Document shortestPathDoc = XmlUtility.getDocumentBuilder().newDocument();
	//					Element successNode = shortestPathDoc.createElement("success");
	//					Element spCommandNode = (Element) shortestPathDoc.importNode(commandNode, true);
	//					Element spParameterNode = (Element) shortestPathDoc.importNode(parameterNode, true);
	//					Element spOutputNode = (Element) shortestPathDoc.importNode(outputNode, true);
	//					successNode.appendChild(spCommandNode);
	//					successNode.appendChild(spParameterNode);
	//					successNode.appendChild(spOutputNode);
	//					shortestPathDoc.appendChild(successNode);
	//					XmlUtility.transform(shortestPathDoc, new File("shortestPath.xsl"), new File(saveHTML + ".html"));
	//				}
	//
	//				if (canvas != null)
	//					canvas.dispose();
	//			}
	//		}
	//		return "success";
	//	}
	//
	//	private Element getDirection(City start, City middle, City end) {
	//		Arc2D.Double arc = new Arc2D.Double();
	//
	//		arc.setArcByTangent(start, middle, end, 5);
	//		double angle = arc.getAngleExtent() > 0 ? arc.getAngleExtent() - 180 : arc.getAngleExtent() + 180;
	//
	//		if (angle > 0 && angle <= 135)
	//			return results.createElement("right");
	//		else if (angle > 135 && angle <= 180 || angle < -135 && angle >= -180)
	//			return results.createElement("straight");
	//		else if (angle < 0 && angle >= -135)
	//			return results.createElement("left");
	//		else 
	//			return null;
	//	}

	/**
	 * Prints the AVL tree.
	 * @throws MyException 
	 */
	private void printAvlTree(Element element, Element outputNode) throws MyException {
		AvlGTree<String, City> cities = spatialMap.getCities(); 

		Element avlNode = results.createElement("AvlGTree");
		avlNode.setAttribute("cardinality", Integer.toString(cities.size()));
		avlNode.setAttribute("maxImbalance", Integer.toString(cities.getG()));
		avlNode.setAttribute("height", Integer.toString(cities.height()));
		printAvlTreeHelper(cities.getRoot(), avlNode);
		outputNode.appendChild(avlNode);
	}

	private void printAvlTreeHelper(AvlNode<String, City> node, Element parent) {
		Element nodeNode;
		if (node == null)
			nodeNode = parent.getOwnerDocument().createElement("emptyChild");
		else {
			nodeNode = parent.getOwnerDocument().createElement("node");

			nodeNode.setAttribute("key", node.getKey());
			nodeNode.setAttribute("value", node.getValue().toString());
			printAvlTreeHelper(node.getLeft(), nodeNode);
			printAvlTreeHelper(node.getRight(), nodeNode);
		}

		parent.appendChild(nodeNode);
	}

	private CanvasPlus drawMap(int remoteX, int remoteY) throws MyException {
		PMQuadtreeNode currNode = spatialMap.getMetropole(remoteX, remoteY).getRoot();

		CanvasPlus map = new CanvasPlus("MeeshQuest", spatialMap.getLocalWidth(), spatialMap.getLocalHeight());
		map.addRectangle(0, 0, spatialMap.getLocalWidth(), spatialMap.getLocalHeight(), Color.black, false);
		drawMapHelper(currNode, map);
		return map;
	}

	private void drawMapHelper(PMQuadtreeNode node, CanvasPlus map) {
		if (node.getType() == PMQuadtreeNode.BLACK) {
			SpatialMapPoint point = ((PMQuadtreeBlack) node).getPoint();
			if (point != null)
				map.addPoint(point.getName(), point.getX(), point.getY(), Color.BLACK);

			Road road;
			for (Geometry2D edge : ((PMQuadtreeBlack) node).getEdges()) {
				road = (Road) edge;
				map.addLine(road.getX1(), road.getY1(), road.getX2(), road.getY2(), Color.BLACK);
			}
		} else if (node.getType() == PMQuadtreeNode.GRAY) {
			PMQuadtreeGray gray = (PMQuadtreeGray) node;
			map.addCross(gray.getRegion(0).getMaxX(), gray.getRegion(0).getY(), gray.getRegion(0).getWidth(), Color.GRAY);

			for (int i = 0; i < 4; i++) {
				drawMapHelper(gray.getChild(i), map);
			}
		}
	}

	public void parseCommands(NodeList commandList) throws IOException, ParserConfigurationException, TransformerException {
		Element commandNode = null;
		Element parameterNode = null;
		Element outputNode = null;

		for (int i = 0; i < commandList.getLength(); i++) {
			if (commandList.item(i) instanceof Element) {
				Element commandElement = (Element) commandList.item(i);

				commandNode = parseCommandNode(commandElement);
				parameterNode = results.createElement("parameters");
				outputNode = results.createElement("output");

				try {
					switch (commandElement.getTagName()) {
					case "createCity":
						createCity(commandElement, parameterNode);
						break;
					case "deleteCity":
						deleteCity(commandElement, parameterNode, outputNode);
						break;
					case "clearAll":
						clearAll();
						break;
					case "listCities":
						listCities(commandElement, parameterNode, outputNode);
						break;
					case "mapRoad":
						mapRoad(commandElement, parameterNode, outputNode);
						break;
					case "mapAirport":
						mapAirport(commandElement, parameterNode);
						break;
					case "mapTerminal":
						mapTerminal(commandElement, parameterNode);
						break;
					case "unmapRoad":
						unmapRoad(commandElement, parameterNode, outputNode);
						break;
					case "unmapAirport":
						unmapAirport(commandElement, parameterNode, outputNode);
						break;
					case "unmapTerminal":
						unmapTerminal(commandElement, parameterNode, outputNode);
						break;
					case "printPMQuadtree":
						printPMQuadtree(commandElement, parameterNode, outputNode);
						break;
					case "saveMap":
						saveMap(commandElement, parameterNode);
						break;
					case "globalRangeCities":
						globalRangeCities(commandElement, parameterNode, outputNode);
						break;
					case "nearestCity":
						nearestCity(commandElement, parameterNode, outputNode);
						break;
					case "mst":
						mst(commandElement, parameterNode, outputNode);
						break;
					case "printAvlTree":
						printAvlTree(commandElement, outputNode);
						break;
					}
					printSuccess(commandNode, parameterNode, outputNode);
				} catch (MyException e) {
					printError(e.getMessage(), commandNode, parameterNode);
				}
			}
		}
	}
}


