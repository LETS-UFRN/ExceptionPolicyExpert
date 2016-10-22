package br.ufrn.lets.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import br.ufrn.lets.exceptionexpert.exception.InvalidRuleSyntaxException;
import br.ufrn.lets.exceptionexpert.models.Rule;
import br.ufrn.lets.exceptionexpert.models.RuleElementPattern;

public class ParseXMLECLRules {

	protected final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static Document parseDocumentFromString(String stringRules) {
		
		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(stringRules));

			Document doc = dBuilder.parse(is);
			
			return doc;
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
		} catch (ParserConfigurationException e) {
			// TODO: handle exception
		}
		
		return null;
	}

	/**
	 * Compile the rules (which is the filePath) to transform into a XML structure 
	 * @param filePath
	 * @return
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document parseDocumentFromXMLFile(String filePath) throws ParserConfigurationException, SAXException, IOException {

		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(fXmlFile);

		return doc;

	}
	
	/**
	 * Parse the XML containing the ECL rules to a collection of Rule objects. Each rule object represents a ECL rule.
	 * 
	 * Ref: http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
	 * @return
	 */
	public static List<Rule> parse(Document document) {

		List<Rule> rules = new ArrayList<Rule>();

		if (document != null) {

			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			document.getDocumentElement().normalize();

			NodeList nList = getAllRules(document);

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Rule objRule = new Rule();

				try {
					Node nNode = nList.item(temp);

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element rule = (Element) nNode;
						Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();

						NodeList exceptions = getExceptions(rule);
						
						for (int i = 0; i < exceptions.getLength(); i++) {
							Node exception = exceptions.item(i);
							
							List<String> listHandlers = new ArrayList<String>();
							NodeList handlers = getHandlers(exception);
							
							for (int j = 0; j < handlers.getLength(); j++) {
								Node handler = handlers.item(j);
								if (handler.getNodeType() == Node.ELEMENT_NODE)
									//To prevent spaces between elements
									//(http://stackoverflow.com/questions/20259742/why-am-i-getting-extra-text-nodes-as-child-nodes-of-root-node)
									listHandlers.add(getHandlerName(handler));
							}

							map.put(getExceptionName(exception), listHandlers);
						}
						
						objRule.setId(getIdElement(rule));
						objRule.setType(isFull(rule)? "full" : "partial");
						objRule.setSignaler(getSignaler(rule));
						objRule.setSignalerPattern(getSignalerPattern(objRule.getSignaler()));
						objRule.setExceptionAndHandlers(map);

						rules.add(objRule);

					}
				} catch (InvalidRuleSyntaxException e) {
					LOGGER.severe(e.getLocalizedMessage());
					LOGGER.severe("Rule " + objRule.getId() + " will not be considered");
				}

			}
		}

		return rules;
	}
	
	public static RuleElementPattern getSignalerPattern(String signaler) throws InvalidRuleSyntaxException {
		if (signaler.compareTo("*") == 0) {
			return RuleElementPattern.ASTERISC_WILDCARD;
		} else if(signaler.endsWith(".*")) {
			return RuleElementPattern.CLASS_DEFINITION;
		} else if(signaler.endsWith("(..)")) {
			return RuleElementPattern.METHOD_DEFINITION;
		}
		throw new InvalidRuleSyntaxException("Invalid format of Signaler element.");
	}
	
	//TODO
	//TODO validate the syntax of all terms
	//TODO
	
	private static NodeList getAllRules(Document doc) {
		return doc.getElementsByTagName("ehrule");
	}
	
	private static String getIdElement(Element rule) {
		return rule.getAttribute("id");
	}
	
	private static boolean isFull(Element rule) {
		return rule.getAttribute("type").equals("full");
	}
	
	private static String getSignaler(Element rule) {
		return rule.getAttribute("signaler");
	}
	
	private static NodeList getExceptions(Element rule) {
		return rule.getElementsByTagName("exception");
	}
	
	private static NodeList getHandlers(Node exception) {
		return exception.getChildNodes();
	}
	
	private static String getHandlerName(Node handler) {
		return handler.getAttributes().getNamedItem("signature").getNodeValue();
	}
	
	private static String getExceptionName(Node exception) {
		return exception.getAttributes().getNamedItem("type").getNodeValue();
	}
	
}
