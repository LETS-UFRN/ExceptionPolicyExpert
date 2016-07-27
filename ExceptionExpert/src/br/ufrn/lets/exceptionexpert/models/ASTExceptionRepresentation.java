package br.ufrn.lets.exceptionexpert.models;

import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ASTExceptionRepresentation {

	PackageDeclaration packageDeclaration;
	
	TypeDeclaration typeDeclaration;

	HandlerClass handlerRepresentation;
	
	SignalerClass signalerRepresentation;
	
	public HandlerClass getHandlerRepresentation() {
		return handlerRepresentation;
	}

	public void setHandlerRepresentation(HandlerClass handlerRepresentation) {
		this.handlerRepresentation = handlerRepresentation;
	}

	public SignalerClass getSignalerRepresentation() {
		return signalerRepresentation;
	}

	public void setSignalerRepresentation(SignalerClass signalerRepresentation) {
		this.signalerRepresentation = signalerRepresentation;
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
}
