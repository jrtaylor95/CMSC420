package cmsc420.meeshquest.part2;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

public class MeeshQuest {

	static Document results;

	private static Commands command;

	public static void main(String[] args) {
		try {
			results = XmlUtility.getDocumentBuilder().newDocument();
		results.appendChild(results.createElement("results"));
			
			parse();
		} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
			printFatalError();
		}
	}

	public static void parse() throws SAXException, IOException, ParserConfigurationException, TransformerException{
		Document doc = XmlUtility.validateNoNamespace(System.in);
		Element ele = doc.getDocumentElement();

		command = new Commands(results, Integer.parseInt(ele.getAttribute("g")), Integer.parseInt(ele.getAttribute("pmOrder")), Integer.parseInt(ele.getAttribute("spatialWidth")), Integer.parseInt(ele.getAttribute("spatialHeight")));
		command.parseCommands(ele.getChildNodes());

		XmlUtility.print(results);
	}

	public static void parse(File file) throws SAXException, IOException, ParserConfigurationException, TransformerException {
		Document doc = XmlUtility.validateNoNamespace(file);
		Element ele = doc.getDocumentElement();

		command = new Commands(results, Integer.parseInt(ele.getAttribute("g")), Integer.parseInt(ele.getAttribute("pmOrder")), Integer.parseInt(ele.getAttribute("spatialWidth")), Integer.parseInt(ele.getAttribute("spatialHeight")));
		command.parseCommands(ele.getChildNodes());

		File myFile = new File("/Users/jrtaylor/Documents/CMSC420Tests/p2 sample tests/" + file.getName().replaceAll("input.xml", "myOutput.xml"));
		XmlUtility.write(results, myFile);
	}

	public static void printFatalError() {
		if (results.hasChildNodes())
			results.removeChild(results.getFirstChild());

		Element error = results.createElement("fatalError");
		results.appendChild(error);
	}
}