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
	 * How many throws statements the class has
	 */
	private int numberOfThrowStatements;
	
	/**
	 * How many catches statements the class has
	 */
	private int numberOfCatchStatements;
	
	/**
	 * Verifies if some method of this class has a throws statement
	 * @return True if exists, false otherwise
	 */
	public boolean hasThrowsStatements() {
		return getNumberOfThrowStatements() > 0;
	}

	/**
	 * Verifies if some method of this class has a catch statement
	 * @return True if exists, false otherwise
	 */
	public boolean hasCatchStatements() {
		return getNumberOfCatchStatements() > 0;
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

	public int getNumberOfThrowStatements() {
		return numberOfThrowStatements;
	}

	public void setNumberOfThrowStatements(int numberOfThrowStatements) {
		this.numberOfThrowStatements = numberOfThrowStatements;
	}

	public int getNumberOfCatchStatements() {
		return numberOfCatchStatements;
	}

	public void setNumberOfCatchStatements(int numberOfCatchStatements) {
		this.numberOfCatchStatements = numberOfCatchStatements;
	}
}
