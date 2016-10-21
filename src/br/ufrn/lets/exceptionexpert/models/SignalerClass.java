package br.ufrn.lets.exceptionexpert.models;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ThrowStatement;

@Deprecated
public class SignalerClass{

	Map<ThrowStatement, Expression> mapThrows = new HashMap<ThrowStatement, Expression>();

	public Map<ThrowStatement, Expression> getMapThrows() {
		return mapThrows;
	}

	public void setMapThrows(Map<ThrowStatement, Expression> mapThrows) {
		this.mapThrows = mapThrows;
	}

	
	
//	Map<MethodDeclaration, List<Name>> mapThrows;

//	public Map<MethodDeclaration, List<Name>> getMapThrows() {
//		return mapThrows;
//	}
//
//	public void setMapThrows(Map<MethodDeclaration, List<Name>> mapThrows) {
//		this.mapThrows = mapThrows;
//	}
	
}
