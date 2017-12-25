package br.ufrn.lets.exceptionexpert.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.ThrowStatement;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.MethodRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.Rule;

/**
 * This class verifies if a method throws an exception improperly. 
 * This happens when there are a rule of "full type" that corresponds to the signaler element. 
 * And this signaler throws an exception that is not list on the rule element "exceptionAndHandlers".
 *
 * This is the first proposed verification.
 */
public class ImproperThrowingVerifier extends ExceptionPolicyVerifier {
	
	private static final String PLUGIN_LOG_IDENTIFIER = "br.ufrn.lets.exceptionExpert";

	public ImproperThrowingVerifier(ASTExceptionRepresentation astRep, ILog log) {
		super(astRep, log);
	}

	@Override
	protected boolean preCondition() {
		//Verifies if the target class has an exception signaler
		return astRep != null && astRep.hasThrowsStatements();
	}
	
	/**
	 * Return the rule name that contains an exception that does match with the exception that the verified method throws, because this is a violation.  
	 * @param excecaoLancadaPeloMetodo
	 * @param rules
	 * @return
	 */
	private String getRuleNameNotMatchWithMethodException(String excecaoLancadaPeloMetodo, List<Rule> rules) {

		for (Rule rule : rules) {
			
			boolean ruleMatch = false;
			
			Map<String, List<String>> exceptionAndHandlers = rule.getExceptionAndHandlers();
			
			Set<String> exceptions = exceptionAndHandlers.keySet();
			for (String ruleException : exceptions) {
				if (ruleException.compareTo(excecaoLancadaPeloMetodo) == 0) {
					ruleMatch = true;
				}
			}
			
			if (!ruleMatch)
				return rule.getId();
		}
		return null;
	}
	
	@Override
	public List<ReturnMessage> verify() {
		
		List<ReturnMessage> returnM = new ArrayList<ReturnMessage>();

		if (preCondition()) {

			Map<MethodRepresentation, List<Rule>> methods = getMapMethodsAndRulesRelatedToSignaler(true);
			
			Set<Entry<MethodRepresentation, List<Rule>>> entrySet = methods.entrySet();
			
			for (Entry<MethodRepresentation, List<Rule>> entry : entrySet) {
				MethodRepresentation method = entry.getKey();
				
				List<ThrowStatement> methodThrowStatements = method.getThrowStatements();

				for(ThrowStatement methodThrow : methodThrowStatements) {
					
					//FIXME - Ver como pegar o nome da excecao a partir do ThrowStatement
					String excecaoLancadaPeloMetodo = methodThrow.getExpression().toString();
					excecaoLancadaPeloMetodo = excecaoLancadaPeloMetodo.replace("new ", "");
					excecaoLancadaPeloMetodo = excecaoLancadaPeloMetodo.split("\\(")[0];
					
					String ruleName = getRuleNameNotMatchWithMethodException(excecaoLancadaPeloMetodo, entry.getValue());
					if (ruleName != null) {

						if (getLog() != null) {
							getLog().log(new Status(Status.WARNING, PLUGIN_LOG_IDENTIFIER, "WARNING - Violation detected (ImproperThrowingVerifier). Rule: " + ruleName + 
									" / Class: " + method.getAstRep().getTypeDeclaration().getName().toString() + 
									" / Method: " + method.getMethodDeclaration().getName().toString() +
									" / Exception: " + excecaoLancadaPeloMetodo));
						}

						ReturnMessage rm = new ReturnMessage();
						rm.setMessage("VIOLATION: should not be throwing the exception " + excecaoLancadaPeloMetodo + " (Policy rule " + ruleName + ")");
						rm.setLineNumber(getAstRep().getAstRoot().getLineNumber(methodThrow.getStartPosition()));
						rm.setMarkerSeverity(IMarker.SEVERITY_WARNING);
						returnM.add(rm);
					}
						
				}
				
			}
			
		}

		return returnM;
	}

}
