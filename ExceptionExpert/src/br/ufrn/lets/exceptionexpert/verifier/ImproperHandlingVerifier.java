package br.ufrn.lets.exceptionexpert.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.ThrowStatement;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.MethodRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.Rule;

/**
 * This class verifies if a method handles an exception improperly. 
 * This happens when there are a rule that determined that the signaler "cannotHandle" this exception.
 *
 * This is the third proposed verification.
 */
public class ImproperHandlingVerifier extends ExceptionPolicyVerifier {
	
	public ImproperHandlingVerifier(ASTExceptionRepresentation astRep) {
		super(astRep);
	}

	@Override
	protected boolean preCondition() {
		//Verifies if the target class has an exception catch
		return astRep != null && astRep.hasCatchStatements();
	}
	
	/**
	 * Return the rule name that contains an exception that does match with the exception that the verified method throws, because this is a violation.  
	 * @param excecaoLancadaPeloMetodo
	 * @param rules
	 * @return
	 */
	private String getRuleNameNotMatchWithMethodException(String excecaoLancadaPeloMetodo, List<Rule> rules) {

		for (Rule rule : rules) {
			Map<String, List<String>> exceptionAndHandlers = rule.getExceptionAndHandlers();
			
			Set<String> exceptions = exceptionAndHandlers.keySet();
			for (String ruleException : exceptions) {
				if (ruleException.compareTo(excecaoLancadaPeloMetodo) != 0) {
					return rule.getId();
				}
			}
		}
		return null;
	}
	
	@Override
	public List<ReturnMessage> verify() {
		
		List<ReturnMessage> returnM = new ArrayList<ReturnMessage>();

		if (preCondition()) {

			Map<MethodRepresentation, List<Rule>> methods = getMapMethodsAndRulesRelatedToSignaler(false);
			
			Set<Entry<MethodRepresentation, List<Rule>>> entrySet = methods.entrySet();
			
			for (Entry<MethodRepresentation, List<Rule>> entry : entrySet) {
				MethodRepresentation method = entry.getKey();
				
				List<ThrowStatement> methodThrowStatements = method.getThrowStatements();

				for(ThrowStatement methodThrow : methodThrowStatements) {
					
					//FIXME - Ver como pegar o nome da excecao a partir do ThrowStatement
					String excecaoLancadaPeloMetodo = methodThrow.getExpression().toString();
					excecaoLancadaPeloMetodo = excecaoLancadaPeloMetodo.replace("new ", "");
					excecaoLancadaPeloMetodo = excecaoLancadaPeloMetodo.replace("()", "");
					
					String ruleName = getRuleNameNotMatchWithMethodException(excecaoLancadaPeloMetodo, entry.getValue());
					if (ruleName != null) {
						
						LOGGER.warning("Violation detected. Rule " + ruleName + " / Class " + method.getAstRep().getTypeDeclaration().getName().toString());
						
						ReturnMessage rm = new ReturnMessage();
						rm.setMessage("VIOLATION: should not be throwing the exception " + excecaoLancadaPeloMetodo + " (Policy rule " + ruleName + ")");
						rm.setLineNumber(getAstRep().getAstRoot().getLineNumber(methodThrow.getStartPosition()));
						returnM.add(rm);
					}
						
				}
				
			}
			
		}

		return returnM;
	}

}
