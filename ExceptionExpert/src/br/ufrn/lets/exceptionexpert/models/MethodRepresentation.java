package br.ufrn.lets.exceptionexpert.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ThrowStatement;

public class MethodRepresentation {

	private ASTExceptionRepresentation astRep;
	
	private MethodDeclaration methodDeclaration;

	private List<CatchClause> catchClauses;

	private List<ThrowStatement> throwStatements;
	
	public MethodRepresentation() {
		setCatchClauses(new ArrayList<CatchClause>());
		throwStatements = new ArrayList<ThrowStatement>();
	}

	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
	}

	public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
		this.methodDeclaration = methodDeclaration;
	}

	public List<ThrowStatement> getThrowStatements() {
		return throwStatements;
	}

	public void setThrowStatements(List<ThrowStatement> throwStatements) {
		this.throwStatements = throwStatements;
	}

	public List<CatchClause> getCatchClauses() {
		return catchClauses;
	}

	public void setCatchClauses(List<CatchClause> catchClauses) {
		this.catchClauses = catchClauses;
	}

	public ASTExceptionRepresentation getAstRep() {
		return astRep;
	}

	public void setAstRep(ASTExceptionRepresentation astRep) {
		this.astRep = astRep;
	}

	
}
