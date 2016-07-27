package br.ufrn.lets.exceptionexpert.models;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SignalerClass{

	Map<MethodDeclaration, List<Name>> mapThrows;

	public Map<MethodDeclaration, List<Name>> getMapThrows() {
		return mapThrows;
	}

	public void setMapThrows(Map<MethodDeclaration, List<Name>> mapThrows) {
		this.mapThrows = mapThrows;
	}
	
}
