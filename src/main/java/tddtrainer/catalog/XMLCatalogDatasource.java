package tddtrainer.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Loads an exercise catalog from an XML file
 * 
 * @author Marcel
 */
public class XMLCatalogDatasource implements CatalogDatasourceIF {

	private InputStream xmlStream = null;
	private ArrayList<Exercise> exercises;

	/**
	 * Creates an XMLCatalogDatasource
	 * 
	 * @param xmlFileURI
	 *            the xml file to read from
	 */
	public XMLCatalogDatasource(InputStream xmlStream) {
		this.xmlStream = xmlStream;
	}

	public XMLCatalogDatasource() {

	}

	@Override
	public void setXmlStream(InputStream xmlStream) {
		this.xmlStream = xmlStream;
	}

	@Override
	public synchronized List<Exercise> loadCatalog() {
		if (exercises != null)
			return exercises;
		exercises = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlStream);

			NodeList exercisesNode = document.getElementsByTagName("exercise");
			for (int i = 0; i < exercisesNode.getLength(); i++) { // iterate
																	// over all
																	// excercises
				Exercise exercise = new Exercise();

				Element exerciseNode = (Element) exercisesNode.item(i);

				exercise.setName(exerciseNode.getAttribute("name"));

				NodeList descriptionNode = exerciseNode.getElementsByTagName("description");
				if (descriptionNode.getLength() < 1) {
					throw new XMLCatalogException("description element is missing");
				}
				exercise.setDescription(descriptionNode.item(0).getTextContent());

				NodeList classesNode = exerciseNode.getElementsByTagName("classes");
				if (classesNode.getLength() < 1) {
					throw new XMLCatalogException("classes element is missing");
				}

				NodeList classes = ((Element) classesNode.item(0)).getElementsByTagName("class");

				for (int k = 0; k < classes.getLength(); k++) {

					Element code = (Element) classes.item(k);

					exercise.setCode(new JavaClass(code.getAttribute("name"), code.getTextContent()));
				}

				NodeList testsNode = exerciseNode.getElementsByTagName("tests");
				if (testsNode.getLength() < 1) {
					throw new XMLCatalogException("tests element is missing");
				}

				NodeList tests = ((Element) testsNode.item(0)).getElementsByTagName("test");

				for (int k = 0; k < tests.getLength(); k++) {

					Element code = (Element) tests.item(k);

					exercise.setTest(new JavaClass(code.getAttribute("name"), code.getTextContent()));
				}

				exercises.add(exercise);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLCatalogException e) {
			e.printStackTrace();
		}
		return exercises;
	}

}
