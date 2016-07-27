package br.ufrn.lets.exceptionexpert.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.Rule;

public class VerifyHandler {

	public static String verify(ASTExceptionRepresentation astRep, List<Rule> rules) {
		StringBuilder s = new StringBuilder();
		
		if (astRep != null && astRep.getHandlerRepresentation() != null && astRep.getHandlerRepresentation().getMapMethodTry() != null) {
			List<Rule> rulesRelatedToSignaler = getRulesRelatedToHandler(astRep, rules);
			
			for(Entry<MethodDeclaration, List<CatchClause>> methodsCatches : astRep.getHandlerRepresentation().getMapMethodTry().entrySet()) {
				
				List<CatchClause> excList = methodsCatches.getValue();
				
				//Verify if some rule is related to exceptions catched by method
				for (CatchClause excName : excList) {
					
					boolean hasRuleRelated = false;
					
					for (Rule r: rules) {
						Entry<String, List<String>> next = r.getExceptionAndHandlers().entrySet().iterator().next();
						String exceptionRuleName = next.getKey();
						List<String> handlersNameRule = next.getValue();
						
						
						if (excName.getException().getType().toString().equals(exceptionRuleName)) {
							//Verify if the actual class is one of the handlers defined on the rule 
							for (String h : handlersNameRule) {
								if (h.equals(astRep.getTypeDeclaration().getName().toString())) {
									hasRuleRelated = true;
								}
							}
							
						}
					}
					
					if (!hasRuleRelated) {
						s.append("============ExcExp====================" + "\n");
						s.append("Method: " + methodsCatches.getKey().getName() + "\n");
						s.append("Exception: " + excName.getException() + "\n");
						s.append("VIOLATION: Cannot catch this exception" + "\n");
						
//						for (Rule r: rulesRelatedToException) {
//							List<String> list = r.getExceptionAndHandlers().get(excName.toString());
//							for (String exc : list){
//								s.append("----> "+exc + "\n");
//							}
//						}
					}
				}
			}
		}
		
		return s.toString();
	}

	private static List<Rule> getRulesRelatedToHandler(ASTExceptionRepresentation astRep, List<Rule> rules) {
		List<Rule> rulesRelated = new ArrayList<>();
		
		String className = astRep.getPackageDeclaration().getName().getFullyQualifiedName() + "." + astRep.getTypeDeclaration().getName().getIdentifier();
		
		//Verify if exists a rule with this signaler
//		for (Rule rule : rules) {
//			rule.getExceptionAndHandlers().get
//			if (rule.getSignaler().equals(className)) {
//				rulesRelated.add(rule);
//			}
//		}

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
