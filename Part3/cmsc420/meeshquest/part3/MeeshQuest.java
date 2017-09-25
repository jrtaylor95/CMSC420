package cmsc420.meeshquest.part3;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import cmsc420.structure.SpatialMap;
import cmsc420.xml.XmlUtility;

public class MeeshQuest {

	static Document results;

	private static Commands command;

	public static void main(String[] args) {
		try {
			results = XmlUtility.getDocumentBuilder().newDocument();
			results.appendChild(results.createElement("results"));

//			parse(new File("/Users/jrtaylor/Documents/CMSC420Tests/sp16p3-sampleTests/part3.testRemove.input.xml"));
			parse();
		} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
			printFatalError();
			e.printStackTrace();
		}
	}

	public static void parse() throws SAXException, IOException, ParserConfigurationException, TransformerException{
		Document doc = XmlUtility.validateNoNamespace(System.in);
		Element ele = doc.getDocumentElement();

		SpatialMap spatialMap = new SpatialMap(Integer.parseInt(ele.getAttribute("remoteSpatialWidth")),
				Integer.parseInt(ele.getAttribute("remoteSpatialHeight")),
				Integer.parseInt(ele.getAttribute("localSpatialWidth")),
				Integer.parseInt(ele.getAttribute("localSpatialHeight")),
				Integer.parseInt(ele.getAttribute("pmOrder")),
				Integer.parseInt(ele.getAttribute("g")));

		command = new Commands(results, spatialMap);
		command.parseCommands(ele.getChildNodes());

		XmlUtility.print(results);
	}

	public static void parse(File file) throws SAXException, IOException, ParserConfigurationException, TransformerException {
		Document doc = XmlUtility.validateNoNamespace(file);
		Element ele = doc.getDocumentElement();

		SpatialMap spatialMap = new SpatialMap(Integer.parseInt(ele.getAttribute("remoteSpatialWidth")),
				Integer.parseInt(ele.getAttribute("remoteSpatialHeight")),
				Integer.parseInt(ele.getAttribute("localSpatialWidth")),
				Integer.parseInt(ele.getAttribute("localSpatialHeight")),
				Integer.parseInt(ele.getAttribute("pmOrder")),
				Integer.parseInt(ele.getAttribute("g")));

		command = new Commands(results, spatialMap);
		command.parseCommands(ele.getChildNodes());

		File myFile = new File("/Users/jrtaylor/Documents/CMSC420Tests/sp16p3-sampleTests/" + file.getName().replaceAll("input.xml", "myOutput.xml"));
		XmlUtility.write(results, myFile);
		XmlUtility.print(results);
	}

	public static void printFatalError() {
		if (results.hasChildNodes())
			results.removeChild(results.getFirstChild());

		Element error = results.createElement("fatalError");
		results.appendChild(error);
	}
}