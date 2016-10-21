package br.ufrn.lets.exceptionexpert.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ASTExceptionRepresentation {
	
	/**
	 * Represents the root node of AST. This node represents this class and all of its elements
	 */
	private CompilationUnit astRoot;

	/**
	 * Package of the class
	 */
	private PackageDeclaration packageDeclaration;
	
	/**
	 * The content of the class
	 */
	private TypeDeclaration typeDeclaration;
	
	/**
	 * The methods of the class
	 */
	private List<MethodRepresentation> methods;
	
	/**
	 * Verifies if some method of this class has a throws statement
	 * @return True if exists, false otherwise
	 */
	public boolean hasThrowsStatements() {
		boolean hasThrows = false;
		
		if (methods != null && methods.size() > 0) {
			for (MethodRepresentation mr : methods) {
				if (mr.getThrowStatements() != null && mr.getThrowStatements().size() > 0) {
					hasThrows = true;
					break;
				}
			}
 		}
		
		return hasThrows;
	}
	
	public ASTExceptionRepresentation() {
		methods = new ArrayList<MethodRepresentation>();
	}

	public PackageDeclaration getPackageDeclaration() {
		return packageDeclaration;
	}

	public void setPackageDeclaration(PackageDeclaration packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
	}

	public TypeDeclaration getTypeDeclaration() {
		return typeDeclaration;
	}

	public void setTypeDeclaration(TypeDeclaration typeDeclaration) {
		this.typeDeclaration = typeDeclaration;
	}

	public CompilationUnit getAstRoot() {
		return astRoot;
	}

	public void setAstRoot(CompilationUnit astRoot) {
		this.astRoot = astRoot;
	}

	public List<MethodRepresentation> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodRepresentation> methods) {
		this.methods = methods;
	}
}
