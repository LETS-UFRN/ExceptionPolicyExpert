package br.ufrn.lets.xml;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.w3c.dom.Document;

import br.ufrn.lets.exceptionexpert.models.RulesRepository;

public class ParseXMLECLRulesTest {

	String xmlRuleTest1 = "<ecl> "
			+ "<ehrule id=\"R1\" type = \"full\" signaler=\"InvalidNameClass\"> "
			+ "<exception type=\"OutraExcecao\">"
			+ "<handler signature=\"p.Class2\" />    	  "
			+ "</exception>"
			+ "</ehrule>"
			+ "</ecl>";
	
	@Test
	public void testDetectViolationAsteriscWildcard() {
		
		Document doc = ParseXMLECLRules.parseDocumentFromString(xmlRuleTest1);
		RulesRepository.setRules(ParseXMLECLRules.parse(doc));
		
		assertTrue(RulesRepository.getRules().isEmpty());

	}


}
