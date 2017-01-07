package br.ufrn.lets.exceptionexpert.models;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;

@Deprecated
public class HandlerClass{

	private Map<MethodDeclaration, List<CatchClause>> mapMethodTry;

	public Map<MethodDeclaration, List<CatchClause>> getMapMethodTry() {
		return mapMethodTry;
	}

	public void setMapMethodTry(Map<MethodDeclaration, List<CatchClause>> mapMethodTry) {
		this.mapMethodTry = mapMethodTry;
	}

	
}
