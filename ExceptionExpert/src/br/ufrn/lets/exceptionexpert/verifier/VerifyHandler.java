package br.ufrn.lets.exceptionexpert.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.Name;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.Rule;

public class VerifyHandler {

	/**
	 * Verify the second rule (Improper handling) - When a method handle an exception, but it should not
	 * @param astRep
	 * @param rules
	 * @return
	 */
	public static List<ReturnMessage> verify(ASTExceptionRepresentation astRep, List<Rule> rules) {
		List<ReturnMessage> returnM = new ArrayList<ReturnMessage>();
		
//		if (astRep != null && astRep.getHandlerRepresentation() != null && astRep.getHandlerRepresentation().getMapMethodTry() != null) {
////			List<Rule> rulesRelatedToSignaler = getRulesRelatedToHandler(astRep, rules);
//			
//			for(Entry<MethodDeclaration, List<CatchClause>> methodsCatches : astRep.getHandlerRepresentation().getMapMethodTry().entrySet()) {
//				
//				List<CatchClause> excList = methodsCatches.getValue();
//				
//				//Verify if some rule is related to exceptions catched by method
//				for (CatchClause excName : excList) {
//					
//					boolean hasRuleRelated = false;
//					
//					for (Rule r: rules) {
//						Entry<String, List<String>> next = r.getExceptionAndHandlers().entrySet().iterator().next();
//						String exceptionRuleName = next.getKey();
//						List<String> handlersNameRule = next.getValue();
//						
//						
//						if (excName.getException().getType().toString().equals(exceptionRuleName)) {
//							//Verify if the actual class is one of the handlers defined on the rule 
//							for (String h : handlersNameRule) {
//								if (h.equals(astRep.getTypeDeclaration().getName().toString())) {
//									hasRuleRelated = true;
//								}
//							}
//							
//						}
//					}
//					
//					if (!hasRuleRelated) {
//						
//						ReturnMessage rm = new ReturnMessage();
//						rm.setMessage("VIOLATION: should not be catching this exception: " + excName.getException());
//						
//						int lineNumber = astRep.getAstRoot().getLineNumber(methodsCatches.getKey().getStartPosition());
//						rm.setLineNumber(lineNumber);
//						
//						returnM.add(rm);
//					}
//				}
//			}
//		}
		
		return returnM;
	}

	private static List<Rule> getRulesRelatedToHandler(ASTExceptionRepresentation astRep, List<Rule> rules) {
		List<Rule> rulesRelated = new ArrayList<Rule>();
		
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
		List<Rule> rulesRelated = new ArrayList<Rule>();

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
