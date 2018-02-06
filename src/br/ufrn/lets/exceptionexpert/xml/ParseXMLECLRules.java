package br.ufrn.lets.exceptionexpert.xml;

import java.io.File;
import java.io.FileNotFoundException;
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import br.ufrn.lets.exceptionexpert.exception.InvalidRuleSyntaxException;
import br.ufrn.lets.exceptionexpert.models.Rule;
import br.ufrn.lets.exceptionexpert.models.RuleElementPatternEnum;
import br.ufrn.lets.exceptionexpert.models.RuleTypeEnum;

public class ParseXMLECLRules {

	private static final String PLUGIN_LOG_IDENTIFIER = "br.ufrn.lets.exceptionExpert";
	protected final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static Document parseDocumentFromString(String stringRules) {

		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(stringRules));

			Document doc = dBuilder.parse(is);

			return doc;

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compile the rules (which is the filePath) to transform into a XML structure 
	 * @param filePath
	 * @param project 
	 * @return
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document parseDocumentFromXMLFile(String filePath, ILog log, IProject project) throws ParserConfigurationException, SAXException, IOException, FileNotFoundException {

		Document doc = null;
		
		try { 
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();

			doc = dBuilder.parse(fXmlFile);

		} catch (FileNotFoundException e) {
			log.log(new Status(Status.ERROR, PLUGIN_LOG_IDENTIFIER, "ERROR - File contract.xml not found for project " + project.getName() + "."));
			throw e;
		} 

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
						Map<String, List<String>> mapCannotHandle = new LinkedHashMap<String, List<String>>();

						NodeList exceptions = getExceptions(rule);

						for (int i = 0; i < exceptions.getLength(); i++) {
							Node exception = exceptions.item(i);

							List<String> listHandlers = new ArrayList<String>();
							List<String> listCannotHandle = new ArrayList<String>();
							NodeList handlers = getHandlers(exception);

							for (int j = 0; j < handlers.getLength(); j++) {
								Node handler = handlers.item(j);
								if (handler.getNodeType() == Node.ELEMENT_NODE)
									//To prevent spaces between elements
									//(http://stackoverflow.com/questions/20259742/why-am-i-getting-extra-text-nodes-as-child-nodes-of-root-node)

									if (handler.getNodeName().compareTo("handler") == 0) {
										listHandlers.add(getHandlerName(handler));

									} else if (handler.getNodeName().compareTo("cannot-handle") == 0) {
										listCannotHandle.add(getHandlerName(handler));

									} else {
										throw new InvalidRuleSyntaxException("Invalid syntax for handler / cannotHandle element");
									}

							}

							if (listHandlers.size() > 0)
								map.put(getExceptionName(exception), listHandlers);

							if (listCannotHandle.size() > 0)
								mapCannotHandle.put(getExceptionName(exception), listCannotHandle);
						}

						objRule.setId(getIdElement(rule));
						objRule.setType(getRuleType(rule));
						objRule.setSignaler(getSignaler(rule));
						objRule.setSignalerPackageDAO(isSignalerPackageDAO(objRule.getSignaler()));
						objRule.setSignalerPackageView(isSignalerPackageView(objRule.getSignaler()));
						objRule.setSignalerPattern(getSignalerPattern(objRule.getSignaler()));
						objRule.setExceptionAndHandlers(map);
						objRule.setExceptionAndCannotHandle(mapCannotHandle);

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

	public static RuleElementPatternEnum getSignalerPattern(String signaler) throws InvalidRuleSyntaxException {
		if (signaler.compareTo("*") == 0) {
			return RuleElementPatternEnum.ASTERISC_WILDCARD;
		} else if(signaler.compareTo("*.dao.*") == 0 || signaler.compareTo("*.jsf.*") == 0) {
			//TODO - should not be with fixed packages. Use regex to identify pattern
			return RuleElementPatternEnum.PACKAGE_DEFINITION;
		} else if(signaler.endsWith(".*")) {
			return RuleElementPatternEnum.CLASS_DEFINITION;
		} else if(signaler.endsWith("(..)")) {
			return RuleElementPatternEnum.METHOD_DEFINITION;
		}
		throw new InvalidRuleSyntaxException("Invalid format of Signaler element.");
	}

	//TODO validate the syntax of all terms

	private static NodeList getAllRules(Document doc) {
		return doc.getElementsByTagName("ehrule");
	}

	private static String getIdElement(Element rule) {
		return rule.getAttribute("id");
	}

	private static RuleTypeEnum getRuleType(Element rule) throws InvalidRuleSyntaxException {
		String attribute = rule.getAttribute("type");
		if (attribute.equals("full"))
			return RuleTypeEnum.FULL;
		else if (attribute.equals("partial"))
			return RuleTypeEnum.PARTIAL;

		throw new InvalidRuleSyntaxException("Invalid value for 'type' property");
	}

	private static String getSignaler(Element rule) {
		return rule.getAttribute("signaler");
	}

	private static boolean isSignalerPackageDAO(String signaler) {
		return signaler.compareTo("*.dao.*") == 0;
	}

	private static boolean isSignalerPackageView(String signaler) {
		return signaler.compareTo("*.jsf.*") == 0;
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
