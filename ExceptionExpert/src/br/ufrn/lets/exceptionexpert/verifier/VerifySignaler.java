package br.ufrn.lets.exceptionexpert.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.Rule;

public class VerifySignaler {

	public static String verify(ASTExceptionRepresentation astRep, List<Rule> rules) {
		StringBuilder s = new StringBuilder();
		
		if (astRep != null && astRep.getSignalerRepresentation() != null && astRep.getSignalerRepresentation().getMapThrows() != null) {
			List<Rule> rulesRelatedToSignaler = getRulesRelatedToSignaler(astRep, rules);
			
			for(Entry<MethodDeclaration, List<Name>> methodsExceptions : astRep.getSignalerRepresentation().getMapThrows().entrySet()) {
				List<Name> excList = methodsExceptions.getValue();
				
				//Verify if some rule is related to exceptions throws by method
				for (Name excName : excList) {
					List<Rule> rulesRelatedToException = getRulesRelatedToException(excName, rulesRelatedToSignaler);
					
					if (!rulesRelatedToException.isEmpty()) {
						s.append("============ExcExp====================" + "\n");
						s.append("Method: " + methodsExceptions.getKey().getName() + "\n");
						s.append("Exception: " + excName + "\n");
						s.append("SHOULD HANDLE:" + "\n");
						
						for (Rule r: rulesRelatedToException) {
							List<String> list = r.getExceptionAndHandlers().get(excName.toString());
							for (String exc : list){
								s.append("----> "+exc + "\n");
							}
						}
					}
				}
			}
		}
		
		return s.toString();
	}

	private static List<Rule> getRulesRelatedToSignaler(ASTExceptionRepresentation astRep, List<Rule> rules) {
		List<Rule> rulesRelated = new ArrayList<>();
		
		String className = astRep.getPackageDeclaration().getName().getFullyQualifiedName() + "." + astRep.getTypeDeclaration().getName().getIdentifier();
		
		//Verify if exists a rule with this signaler
		for (Rule rule : rules) {
			if (rule.getSignaler().equals(className)) {
				rulesRelated.add(rule);
			}
		}

		return rulesRelated;
	}
	
	private static List<Rule> getRulesRelatedToException(Name exceptionName, List<Rule> rules) {		
		List<Rule> rulesRelated = new ArrayList<>();

		//Verify if exists a rule with this signaler
		for (Rule rule : rules) {
			Map<String, List<String>> exceptionAndHandlers = rule.getExceptionAndHandlers();
			
			for (Entry<String, List<String>> handler: exceptionAndHandlers.entrySet()) {
				if (handler.getKey().equals(exceptionName.toString())) {
					rulesRelated.add(rule);
				}
			}
		}

		return rulesRelated;
	}

}
