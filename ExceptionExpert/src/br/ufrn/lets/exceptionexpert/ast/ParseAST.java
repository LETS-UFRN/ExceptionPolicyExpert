package br.ufrn.lets.exceptionexpert.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.HandlerClass;
import br.ufrn.lets.exceptionexpert.models.SignalerClass;

public class ParseAST {

	public static CompilationUnit parse(ICompilationUnit cu) {
		
		CompilationUnit astRoot = parseUnit(cu);
		
		return astRoot;
		
	}

	private static CompilationUnit parseUnit(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit); // set source
		parser.setResolveBindings(true); // we need bindings later on
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
	}
	
	public static ASTExceptionRepresentation getThrowsStatement(CompilationUnit astRoot) {
		
		final ASTExceptionRepresentation astRep = new ASTExceptionRepresentation();
		
		final SignalerClass signaler = new SignalerClass();
		
		final HandlerClass handler = new HandlerClass();
		
		final Map<MethodDeclaration, List<Name>> mapThrows = new HashMap<>();
		
		final Map<MethodDeclaration, List<CatchClause>> mapMethodTry = new HashMap<>();
		
		//Ref: http://www.programcreek.com/2012/06/insertadd-statements-to-java-source-code-by-using-eclipse-jdt-astrewrite/
//		TypeDeclaration typeDecl = (TypeDeclaration) astRoot.types().get(0);
//		MethodDeclaration methodDecl = typeDecl.getMethods()[0]; //TODO aqui so pega o primeiro metodo. Iterar sobre todos
//		Block block = methodDecl.getBody();
//		System.out.println(block);
		
		//Ref: http://www.programcreek.com/java-api-examples/index.php?api=org.eclipse.jdt.core.dom.MethodDeclaration
		
		final CompilationUnit astRootFinal = astRoot;

		astRootFinal.accept(new ASTVisitor() {
			 
			public boolean visit(CompilationUnit node) {
				astRep.setPackageDeclaration(node.getPackage());
				return true;
			}

			public boolean visit(TypeDeclaration node) {
				astRep.setTypeDeclaration(node);
				return true;
			}
 
			public boolean visit(MethodDeclaration node) {
				
				int lineNumber = astRootFinal.getLineNumber(node.getStartPosition());
				
				SimpleName name = node.getName();
				List<Name> thrownExceptionTypes = node.thrownExceptions();
				
				mapThrows.put(node, thrownExceptionTypes);
				System.out.println("Metodo " + name);
				System.out.println("lineNumber " + lineNumber);
				
				System.out.println("Exceptions: '" + thrownExceptionTypes);
				return true;
			}

			public boolean visit(CatchClause node) {

				MethodDeclaration md = (MethodDeclaration) node.getParent().getParent().getParent();
				
				List<CatchClause> listTry = mapMethodTry.get(md);
				if (listTry == null)
					listTry = new ArrayList<>();
				listTry.add(node);
				
				mapMethodTry.put(md, listTry);
				
				return true;
			}
			
		});
		
		if (!mapThrows.isEmpty()) {
			signaler.setMapThrows(mapThrows);
			astRep.setSignalerRepresentation(signaler);

			handler.setMapMethodTry(mapMethodTry);
			astRep.setHandlerRepresentation(handler);
			
			return astRep;
		}

		return null;
	}
	
}
