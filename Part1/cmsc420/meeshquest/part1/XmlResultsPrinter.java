package cmsc420.meeshquest.part1;

import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlResultsPrinter {
	public static void createCityError(Document document, String type, String name, int x, int y, int radius, String color) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "createCity");

		Element parameters = document.createElement("parameters");

		Element nameParam = document.createElement("name");
		nameParam.setAttribute("value", name);
		Element xParam = document.createElement("x");
		xParam.setAttribute("value", Integer.toString(x));
		Element yParam = document.createElement("y");
		yParam.setAttribute("value", Integer.toString(y));
		Element radiusParam = document.createElement("radius");
		radiusParam.setAttribute("value", Integer.toString(radius));
		Element colorParam = document.createElement("color");
		colorParam.setAttribute("value", color);
		parameters.appendChild(nameParam);
		parameters.appendChild(xParam);
		parameters.appendChild(yParam);
		parameters.appendChild(radiusParam);
		parameters.appendChild(colorParam);

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void createCitySuccess(Document document, String name, int x, int y, int radius, String color) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "createCity");

		Element parameters = document.createElement("parameters");

		Element nameParam = document.createElement("name");
		nameParam.setAttribute("value", name);
		Element xParam = document.createElement("x");
		xParam.setAttribute("value", Integer.toString(x));
		Element yParam = document.createElement("y");
		yParam.setAttribute("value", Integer.toString(y));
		Element radiusParam = document.createElement("radius");
		radiusParam.setAttribute("value", Integer.toString(radius));
		Element colorParam = document.createElement("color");
		colorParam.setAttribute("value", color);
		parameters.appendChild(nameParam);
		parameters.appendChild(xParam);
		parameters.appendChild(yParam);
		parameters.appendChild(radiusParam);
		parameters.appendChild(colorParam);

		Element output = document.createElement("output");

		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);

		document.getFirstChild().appendChild(success);
	}

	public static void deleteCityError(Document document, String type, String name) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "deleteCity");

		Element parameters = document.createElement("parameters");
		Element nameParam = document.createElement("name");
		nameParam.setAttribute("value", name);
		parameters.appendChild(nameParam);

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void deleteCitySuccess(Document document, City city, boolean cityUnmapped) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "deleteCity");

		Element parameters = document.createElement("parameters");
		Element nameParam = document.createElement("name");
		nameParam.setAttribute("value", city.getName());
		parameters.appendChild(nameParam);

		Element output = document.createElement("output");
		
		if (cityUnmapped) {
			Element unmapped = document.createElement("cityUnmapped");
			unmapped.setAttribute("name", city.getName());
			unmapped.setAttribute("x", Integer.toString((int) city.x));
			unmapped.setAttribute("y", Integer.toString((int) city.y));
			unmapped.setAttribute("radius", Integer.toString((int) city.getRadius()));
			unmapped.setAttribute("color", city.getColor());
			output.appendChild(unmapped);
		}
		
		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);

		document.getFirstChild().appendChild(success);
	}

	public static void clearAllSuccess(Document document) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "clearAll");

		Element parameters = document.createElement("parameters");

		Element output = document.createElement("output");
		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);

		document.getFirstChild().appendChild(success);
	}

	public static void listCitiesError(Document document, String type, String sortBy) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "listCities");

		Element parameters = document.createElement("parameters");
		Element sortByParam = document.createElement("sortBy");
		sortByParam.setAttribute("value", sortBy);
		parameters.appendChild(sortByParam);

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void listCitiesSuccess(Document document, TreeMap<String, City> stringCityMap, TreeSet<City> citySet, String sortBy) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "listCities");

		Element parameters = document.createElement("parameters");
		Element sortByParam = document.createElement("sortBy");
		sortByParam.setAttribute("value", sortBy);
		parameters.appendChild(sortByParam);

		Element output = document.createElement("output");
		Element cityList = document.createElement("cityList");
		//List cities
		Element city;
		City v;
		if (sortBy.equals("name")) {
			for (String name: stringCityMap.keySet()) {
				v = stringCityMap.get(name);
				city = v.printToXml(document);
				cityList.appendChild(city);
			}
		} else if (sortBy.equals("coordinate")) {
			for (City c: citySet) {
				city = c.printToXml(document);
				cityList.appendChild(city);
			}
		}
		output.appendChild(cityList);

		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);

		document.getFirstChild().appendChild(success);
	}

	public static void mapCityError(Document document, String type, String name) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "mapCity");

		Element parameters = document.createElement("parameters");
		Element nameParam = document.createElement("name");
		nameParam.setAttribute("value", name);
		parameters.appendChild(nameParam);

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void mapCitySuccess(Document document, String name) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "mapCity");

		Element parameters = document.createElement("parameters");
		Element nameParam = document.createElement("name");
		nameParam.setAttribute("value", name);
		parameters.appendChild(nameParam);

		Element output = document.createElement("output");

		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);

		document.getFirstChild().appendChild(success);
	}

	public static void unmapCityError(Document document, String type, String name) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "unmapCity");

		Element parameters = document.createElement("parameters");
		Element nameParam = document.createElement("name");
		nameParam.setAttribute("value", name);
		parameters.appendChild(nameParam);

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void unmapCitySuccess(Document document, String name) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "unmapCity");

		Element parameters = document.createElement("parameters");
		Element nameParam = document.createElement("name");
		nameParam.setAttribute("value", name);
		parameters.appendChild(nameParam);

		Element output = document.createElement("output");

		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);

		document.getFirstChild().appendChild(success);
	}

	public static void printPRQuadtreeError(Document document, String type) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "printPRQuadtree");

		Element parameters = document.createElement("parameters");

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void printPRQuadtreeSuccess(Document document, PRQuadtree<City> spatialMap) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "printPRQuadtree");

		Element parameters = document.createElement("parameters");

		Element output = document.createElement("output");

		Element quadtree = spatialMap.printToXml(document);
		output.appendChild(quadtree);

		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);
		document.getFirstChild().appendChild(success);
	}

	public static void saveMapSuccess(Document document, String name) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "saveMap");

		Element parameters = document.createElement("parameters");
		Element nameParam = document.createElement("name");
		nameParam.setAttribute("value", name);
		parameters.appendChild(nameParam);

		Element output = document.createElement("output");

		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);


		document.getFirstChild().appendChild(success);
	}

	public static void rangeCitiesError(Document document, String type, int x, int y, int radius) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "rangeCities");

		Element parameters = document.createElement("parameters");
		Element xParam = document.createElement("x");
		xParam.setAttribute("value", Integer.toString(x));
		Element yParam = document.createElement("y");
		yParam.setAttribute("value", Integer.toString(y));
		Element radiusParam = document.createElement("radius");
		radiusParam.setAttribute("value", Integer.toString(radius));
		parameters.appendChild(xParam);
		parameters.appendChild(yParam);
		parameters.appendChild(radiusParam);

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void rangeCitiesSuccess(Document document, PriorityQueue<City> inRange, int x, int y, int radius) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "rangeCities");

		Element parameters = document.createElement("parameters");
		Element xParam = document.createElement("x");
		xParam.setAttribute("value", Integer.toString(x));
		Element yParam = document.createElement("y");
		yParam.setAttribute("value", Integer.toString(y));
		Element radiusParam = document.createElement("radius");
		radiusParam.setAttribute("value", Integer.toString(radius));
		parameters.appendChild(xParam);
		parameters.appendChild(yParam);
		parameters.appendChild(radiusParam);

		Element output = document.createElement("output");
		Element cityList = document.createElement("cityList");

		Element city;
		City c;
		while (!inRange.isEmpty()) {
			c = inRange.remove();
			city = c.printToXml(document);
			cityList.appendChild(city);
		}
		output.appendChild(cityList);

		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);

		document.getFirstChild().appendChild(success);
	}

	public static void rangeCitiesError(Document document, String type, int x, int y, int radius, String saveMap) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "rangeCities");

		Element parameters = document.createElement("parameters");
		Element xParam = document.createElement("x");
		xParam.setAttribute("value", Integer.toString(x));
		Element yParam = document.createElement("y");
		yParam.setAttribute("value", Integer.toString(y));
		Element radiusParam = document.createElement("radius");
		radiusParam.setAttribute("value", Integer.toString(radius));
		Element saveMapParam = document.createElement("saveMap");
		saveMapParam.setAttribute("value", saveMap);
		parameters.appendChild(xParam);
		parameters.appendChild(yParam);
		parameters.appendChild(radiusParam);
		parameters.appendChild(saveMapParam);

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void rangeCitiesSuccess(Document document, PriorityQueue<City> inRange, int x, int y, int radius, String saveMap) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "rangeCities");

		Element parameters = document.createElement("parameters");
		Element xParam = document.createElement("x");
		xParam.setAttribute("value", Integer.toString(x));
		Element yParam = document.createElement("y");
		yParam.setAttribute("value", Integer.toString(y));
		Element radiusParam = document.createElement("radius");
		radiusParam.setAttribute("value", Integer.toString(radius));
		Element saveMapParam = document.createElement("saveMap");
		saveMapParam.setAttribute("value", saveMap);
		parameters.appendChild(xParam);
		parameters.appendChild(yParam);
		parameters.appendChild(radiusParam);
		parameters.appendChild(saveMapParam);

		Element output = document.createElement("output");
		Element cityList = document.createElement("cityList");

		Element city;
		City c;
		while (!inRange.isEmpty()) {
			c = inRange.remove();
			city = c.printToXml(document);
			cityList.appendChild(city);
		}
		output.appendChild(cityList);

		//List cities
		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);

		document.getFirstChild().appendChild(success);
	}

	public static void nearestCityError(Document document, String type, int x, int y) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "nearestCity");

		Element parameters = document.createElement("parameters");
		Element xParam = document.createElement("x");
		xParam.setAttribute("value", Integer.toString(x));
		Element yParam = document.createElement("y");
		yParam.setAttribute("value", Integer.toString(y));

		parameters.appendChild(xParam);
		parameters.appendChild(yParam);

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void nearestCitySuccess(Document document, PRQuadtree<City> spatialMap, int x, int y) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "nearestCity");

		Element parameters = document.createElement("parameters");
		Element xParam = document.createElement("x");
		xParam.setAttribute("value", Integer.toString(x));
		Element yParam = document.createElement("y");
		yParam.setAttribute("value", Integer.toString(y));
		parameters.appendChild(xParam);
		parameters.appendChild(yParam);

		Element output = document.createElement("output");
		City c = spatialMap.getNearest(x, y);
		Element city = c.printToXml(document);
		output.appendChild(city);

		//List cities
		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);

		document.getFirstChild().appendChild(success);
	}

	public static void printAvlTreeError(Document document, String type) {
		Element error = document.createElement("error");
		error.setAttribute("type", type);

		Element command = document.createElement("command");
		command.setAttribute("name", "printAvlTree");

		Element parameters = document.createElement("parameters");

		error.appendChild(command);
		error.appendChild(parameters);

		document.getFirstChild().appendChild(error);
	}

	public static void printAvlTreeSuccess(Document document, AvlTree cityTree) {
		Element success = document.createElement("success");

		Element command = document.createElement("command");
		command.setAttribute("name", "printAvlTree");

		Element parameters = document.createElement("parameters");

		Element output = document.createElement("output");
		Element avlgTree = cityTree.printToXML(document);

		output.appendChild(avlgTree);

		success.appendChild(command);
		success.appendChild(parameters);
		success.appendChild(output);
		document.getFirstChild().appendChild(success);
	}

	public static void printFatalError(Document document) {
		if (document.getFirstChild() != null)
			document.removeChild(document.getFirstChild());
		
		Element error = document.createElement("fatalError");
		document.appendChild(error);
	}
	
	public static void printUndefinedError(Document document) {
		Element error = document.createElement("undefinedError");
		document.appendChild(error);
	}
}
