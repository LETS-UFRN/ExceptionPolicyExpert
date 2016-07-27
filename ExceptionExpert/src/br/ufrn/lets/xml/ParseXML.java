package br.ufrn.lets.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.ufrn.lets.exceptionexpert.models.Rule;

public class ParseXML {

	/**
	 * Ref: http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
	 */
	public static List<Rule> parse() {
		List<Rule> rules = new ArrayList<>();

		try {
			
			String path = "/Users/taiza/git/ExceptionExpert/ExceptionExpert/resources/contract.xml";
			
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(fXmlFile);

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			NodeList nList = getAllRules(doc);

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Rule objRule = new Rule();
				
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element rule = (Element) nNode;
					
					List<String> listHandlers = new ArrayList<>();
					
					NodeList handlers = getHandlers(rule);
					
					for (int j = 0; j < handlers.getLength(); j++) {
						listHandlers.add(getHandler((Element) handlers.item(j)));
					}
					
					Map<String, List<String>> map = new HashMap<>();
					map.put(getException(rule), listHandlers);
					
					objRule.setType(isFull(rule)? "full" : "partial");
					objRule.setSignaler(getSignaler(rule));
					objRule.setExceptionAndHandlers(map);
					
//					objRule.toString();
					
					rules.add(objRule);
					
				}
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rules;
	}
	
	public static NodeList getAllRules(Document doc) {
		return doc.getElementsByTagName("ehrule");
	}
	
	public static boolean isFull(Element rule) {
		return rule.getAttribute("type").equals("full");
	}
	
	public static String getSignaler(Element rule) {
		return rule.getAttribute("signaler");
	}
	
	public static String getException(Element rule) {
		Node item = rule.getElementsByTagName("exception").item(0);
		return ((Element) item).getAttribute("type");
	}
	
	public static NodeList getHandlers(Element rule) {
		return rule.getElementsByTagName("handler");
	}
	
	public static String getHandler(Element handler) {
		return handler.getAttribute("signature");
	}
	
	
}
