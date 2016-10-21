package br.ufrn.lets.exceptionexpert.verifier;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

public class VerifyHandlerTest {
	
	ASTExceptionRepresentation astRep;
	
	String javaSourceTest1 = 
			  "package p; "
			+ "public class Class2 {"
			+ "		public void chamarClasse1() { "
			+ "			try { "
			+ "				Class teste = new Class(); "
			+ "				teste.umMetodoQualquer(null); "
			+ "			}"
			+ " 		catch(ExtensaoUmaExcecao exc) {"
			+ "			} "
			+ "		}"
			+ "}";
	
	String xmlRuleTest1 = "";
	
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
	public void testDetectViolation() {
		populateAST(javaSourceTest1);
		List<ReturnMessage> verifyResult = VerifyHandler.verify(astRep, RulesRepository.getRules());
		
		fail("Implementar o cannotHandle");
		
		assertEquals(verifyResult.get(0).getMessage(), "VIOLATION: should not be catching this exception: ExtensaoUmaExcecao exc");
	}

}
