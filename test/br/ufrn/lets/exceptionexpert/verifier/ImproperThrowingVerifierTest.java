package br.ufrn.lets.exceptionexpert.verifier;


import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import br.ufrn.lets.exceptionexpert.ast.ParseAST;
import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.RulesRepository;
import br.ufrn.lets.xml.ParseXMLECLRules;

public class ImproperThrowingVerifierTest {
	
	ASTExceptionRepresentation astRep;
	
	String javaSourceTest1 = 
			  "package p; "
			+ "public class Class {"
			+ "		public void umMetodoQualquer() throws UmaExcecao { "
			+ "			throw new UmaExcecao(); "
			+ "		}"
			+ "}";

	String javaSourceTest2 = 
			  "package p; "
			+ "public class Class1 {"
			+ "		public void method1() throws UmaExcecao { "
			+ "			throw new UmaExcecao(); "
			+ "		}"
			+ "		public void method2() throws UmaExcecao { "
			+ "			throw new UmaExcecao(); "
			+ "		}"
			+ "}";

	String javaSourceTest3 = 
			  "package p; "
			+ "public class Class2 {"
			+ "		public void method1() throws UmaExcecao { "
			+ "			throw new UmaExcecao(); "
			+ "		}"
			+ "		public void method2() throws OutraExcecao { "
			+ "			throw new OutraExcecao(); "
			+ "		}"
			+ "}";

	String xmlRuleTest1 = "<ecl> "
			+ "<ehrule type = \"full\" signaler=\"p.Class.*\"> "
			+ "<exception type=\"OutraExcecao\">"
			+ "<handler signature=\"p.Class2\" />    	  "
			+ "</exception>"
			+ "</ehrule>"
			+ "<ehrule type = \"full\" signaler=\"p.Class1.*\"> "
			+ "<exception type=\"OutraExcecao\">"
			+ "<handler signature=\"p.Class2\" />    	  "
			+ "</exception>"
			+ "</ehrule>"
			+ "<ehrule type = \"full\" signaler=\"p.Class2.method1(..)\"> "
			+ "<exception type=\"OutraExcecao\">"
			+ "<handler signature=\"p.Class2\" />    	  "
			+ "</exception>"
			+ "</ehrule>"

			+ "</ecl>";
	
	private void populateAST(String javaSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(javaSource.toCharArray());
		CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
		
		astRep = ParseAST.parseClassASTToExcpetionRep(astRoot);
	}
	
	private void populateECLRules() {
		Document doc = ParseXMLECLRules.parseDocumentFromString(xmlRuleTest1);
		RulesRepository.setRules(ParseXMLECLRules.parse(doc));
	}
	
	@Before
	public void before() {
		populateECLRules();
	}
	
	@Test
	public void testDetectViolationAsteriscWildcard() {
		populateAST(javaSourceTest1);
		
		ImproperThrowingVerifier improperThrowingVerifier = new ImproperThrowingVerifier(astRep);
		List<ReturnMessage> verifyResult = improperThrowingVerifier.verify();
		
		assertEquals(verifyResult.get(0).getMessage(), "VIOLATION: should not be throwing the exception UmaExcecao");
	}
	
	@Test
	public void testDetectViolationClassDefinition() {
		populateAST(javaSourceTest2);
		
		ImproperThrowingVerifier improperThrowingVerifier = new ImproperThrowingVerifier(astRep);
		List<ReturnMessage> verifyResult = improperThrowingVerifier.verify();
		
		assertEquals(verifyResult.get(0).getMessage(), "VIOLATION: should not be throwing the exception UmaExcecao");
	}
	
	@Test
	public void testDetectViolationMethodDefinition() {
		populateAST(javaSourceTest3);
		
		ImproperThrowingVerifier improperThrowingVerifier = new ImproperThrowingVerifier(astRep);
		List<ReturnMessage> verifyResult = improperThrowingVerifier.verify();
		
		assertEquals(verifyResult.get(0).getMessage(), "VIOLATION: should not be throwing the exception UmaExcecao");
	}

}
