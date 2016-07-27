package br.ufrn.lets.exceptionexpert.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.HandlerClass;
import br.ufrn.lets.exceptionexpert.models.SignalerClass;

public class ParseAST {

	public static CompilationUnit parse() {
		
		IJavaElement activeEditorJavaInput = EditorUtility.getActiveEditorJavaInput();
		
		CompilationUnit astRoot = parseUnit((ICompilationUnit) activeEditorJavaInput);
		
		return astRoot;
//		return parse.getAST();
		
//		try {
//			getIMethods(compilationUnit);
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

	private static CompilationUnit parseUnit(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit); // set source
		parser.setResolveBindings(true); // we need bindings later on
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
	}
	
	public static String getCurrentClassName(CompilationUnit ast) {
		String className = "";
		
		return className;
	}

	private static List<IMethod> getMethods(ICompilationUnit unit) {
		List<IMethod> listOfMethods = new ArrayList<>();
		
		try {
			IType[] allTypes = unit.getAllTypes();
			for (IType type : allTypes) {
				IMethod[] methods = type.getMethods();
				for (IMethod method : methods) {
					listOfMethods.add(method);
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listOfMethods;
		
	}
	
	private static void getIMethods(ICompilationUnit unit) throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			IMethod[] methods = type.getMethods();
			for (IMethod method : methods) {

				System.out.println("Method name " + method.getElementName());
				System.out.println("Method ex types " + method.getExceptionTypes());
				System.out.println("Method handle identifier " + method.getHandleIdentifier());


				//				System.out.println("Signature " + method.getSignature());
				//				System.out.println("Return Type " + method.getReturnType());

			}
		}
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
				
				SimpleName name = node.getName();
				List<Name> thrownExceptionTypes = node.thrownExceptions();
				
				mapThrows.put(node, thrownExceptionTypes);
				System.out.println("Metodo " + name);
				
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
