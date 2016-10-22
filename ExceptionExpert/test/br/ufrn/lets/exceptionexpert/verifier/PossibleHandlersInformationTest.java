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

public class PossibleHandlersInformationTest {
	
	ASTExceptionRepresentation astRep;
	
	String javaSourceTest1 = 
			  "package p; \n"
			+ "public class Class {\n"
			+ "		public void umMetodoQualquer() throws UmaExcecao { \n"
			+ "			throw new UmaExcecao(); \n"
			+ "		}"
			+ "}";

	String javaSourceTest2 = 
			  "package p; \n"
			+ "public class Class1 \n{"
			+ "		public void method1() throws UmaExcecao { \n"
			+ "			throw new UmaExcecao(); \n"
			+ "		}\n"
			+ "}";

	String javaSourceTest3 = 
			  "package p; \n"
			+ "public class Class2 {\n"
			+ "		public void method1() throws UmaExcecao { \n"
			+ "			throw new UmaExcecao(); \n"
			+ "		}\n"
			+ "		public void method2() throws OutraExcecao { \n"
			+ "			throw new OutraExcecao(); \n"
			+ "		}\n"
			+ "}";

	String javaSourceTest4 = 
			  "package anotherPackage.p; \n"
			+ "public class Class2 {\n"
			+ "		public void method1() throws UmaExcecao { \n"
			+ "			throw new UmaExcecao(); \n"
			+ "		}\n"
			+ "}";

	String xmlRuleTest1 = "<ecl> "
			+ "<ehrule id=\"R1\" type = \"full\" signaler=\"p.Class.*\"> "
				+ "<exception type=\"UmaExcecao\">"
				+ "<handler signature=\"p.Class2\" />    	  "
				+ "</exception>"
			+ "</ehrule>"
			+ "<ehrule id=\"R2\" type = \"full\" signaler=\"p.Class1.*\"> "
				+ "<exception type=\"UmaExcecao\">"
				+ "<handler signature=\"ClassA\" />    	  "
				+ "<handler signature=\"ClassB.*\" />    	  "
				+ "<handler signature=\"ClassC.method1(..)\" />    	  "
				+ "</exception>"
			+ "</ehrule>"
			+ "<ehrule id=\"R3\" type = \"full\" signaler=\"p.Class2.*\"> "
				+ "<exception type=\"UmaExcecao\">"
				+ "<handler signature=\"ClassA\" />    	  "
				+ "<handler signature=\"ClassB.*\" />    	  "
				+ "<handler signature=\"ClassC.method1(..)\" />    	  "
				+ "</exception>"
				+ "<exception type=\"OutraExcecao\">"
				+ "<handler signature=\"ClassA2\" />    	  "
				+ "<handler signature=\"ClassB2.*\" />    	  "
				+ "<handler signature=\"ClassC2.method1(..)\" />    	  "
				+ "</exception>"
			+ "</ehrule>"
			+ "<ehrule id=\"R4\" type = \"partial\" signaler=\"anotherPackage.p.Class2.method1(..)\"> "
				+ "<exception type=\"UmaExcecao\">"
				+ "<handler signature=\"p.ClassHandler\" />    	  "
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
	public void testDetectOneHandler() {
		populateAST(javaSourceTest1);
		
		PossibleHandlersInformation possibleHandlersInformation = new PossibleHandlersInformation(astRep);
		List<ReturnMessage> verifyResult = possibleHandlersInformation.verify();
		
		assertEquals(1, verifyResult.size());
		assertEquals(verifyResult.get(0).getMessage(), "Should be caught by (Policy rule R1): [p.Class2]");
		assertEquals(verifyResult.get(0).getLineNumber(), new Integer(4));

	}
	
	@Test
	public void testDetectMoreThanOneHandler() {
		populateAST(javaSourceTest2);
		
		PossibleHandlersInformation possibleHandlersInformation = new PossibleHandlersInformation(astRep);
		List<ReturnMessage> verifyResult = possibleHandlersInformation.verify();
		
		assertEquals(1, verifyResult.size());
		assertEquals(verifyResult.get(0).getMessage(), "Should be caught by (Policy rule R2): [ClassA, ClassB.*, ClassC.method1(..)]");
		assertEquals(verifyResult.get(0).getLineNumber(), new Integer(4));

	}
	
	@Test
	public void testDetectMoreThanOneException() {
		populateAST(javaSourceTest3);
		
		PossibleHandlersInformation possibleHandlersInformation = new PossibleHandlersInformation(astRep);
		List<ReturnMessage> verifyResult = possibleHandlersInformation.verify();
		
		assertEquals(2, verifyResult.size());
		assertEquals(verifyResult.get(0).getMessage(), "Should be caught by (Policy rule R3): [ClassA, ClassB.*, ClassC.method1(..)]");
		assertEquals(verifyResult.get(0).getLineNumber(), new Integer(4));

		assertEquals(verifyResult.get(1).getMessage(), "Should be caught by (Policy rule R3): [ClassA2, ClassB2.*, ClassC2.method1(..)]");
		assertEquals(verifyResult.get(1).getLineNumber(), new Integer(7));

	}
	
	@Test
	public void testPartialRule() {
		populateAST(javaSourceTest4);
		
		PossibleHandlersInformation possibleHandlersInformation = new PossibleHandlersInformation(astRep);
		List<ReturnMessage> verifyResult = possibleHandlersInformation.verify();
		
		assertEquals(1, verifyResult.size());
		assertEquals(verifyResult.get(0).getMessage(), "Should be caught by (Policy rule R4): [p.ClassHandler]");
		assertEquals(verifyResult.get(0).getLineNumber(), new Integer(4));


	}
}
