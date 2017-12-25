package br.ufrn.lets.exceptionexpert.verifier;

import java.util.LinkedList;
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
 * This class is responsible for verify the exceptions a methods throws and which are the possible handlers. 
 * This happens when there are a rule of "full or partial type" that corresponds to the signaler element. 
 * The verifier returns a list of elements that may handle the exception. 
 * 
 * This is the fourth proposed verification.
 */
public class PossibleHandlersInformation extends ExceptionPolicyVerifier {
	
	private static final String PLUGIN_LOG_IDENTIFIER = "br.ufrn.lets.exceptionExpert";

	public PossibleHandlersInformation(ASTExceptionRepresentation astRep, ILog log) {
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
	private Rule getRuleNameMatchWithMethodException(String excecaoLancadaPeloMetodo, List<Rule> rules) {

		for (Rule rule : rules) {
			Map<String, List<String>> exceptionAndHandlers = rule.getExceptionAndHandlers();
			
			Set<String> exceptions = exceptionAndHandlers.keySet();
			for (String ruleException : exceptions) {
				if (ruleException.compareTo(excecaoLancadaPeloMetodo) == 0) {
					return rule;
				}
			}
		}
		return null;
	}
	
	private String formatHandler(List<String> handlers) {
		String formattedHandlers= "[";
		
		for (int i= 0; i< handlers.size(); i++) {
			formattedHandlers += handlers.get(i);
			
			if (i < handlers.size() - 1)
				formattedHandlers += ", ";
		}
		
		formattedHandlers += "]";
		
		return formattedHandlers;
	}
	
	@Override
	public List<ReturnMessage> verify() {
		
		List<ReturnMessage> returnM = new LinkedList<ReturnMessage>();

		if (preCondition()) {

			Map<MethodRepresentation, List<Rule>> methods = getMapMethodsAndRulesRelatedToSignaler(false);
			
			Set<Entry<MethodRepresentation, List<Rule>>> entrySet = methods.entrySet();
			
			for (Entry<MethodRepresentation, List<Rule>> entry : entrySet) {
				MethodRepresentation method = entry.getKey();
				
				List<ThrowStatement> methodThrowStatements = method.getThrowStatements();

				for(ThrowStatement methodThrow : methodThrowStatements) {
					//Vejo se tem algum excpetion que bate com essa excecao
					
					//FIXME - Ver como pegar o nome da excecao a partir do ThrowStatement
					String excecaoLancadaPeloMetodo = methodThrow.getExpression().toString();
					excecaoLancadaPeloMetodo = excecaoLancadaPeloMetodo.replace("new ", "");
					excecaoLancadaPeloMetodo = excecaoLancadaPeloMetodo.split("\\(")[0];
					
					Rule ruleName = getRuleNameMatchWithMethodException(excecaoLancadaPeloMetodo, entry.getValue());
					
					if (ruleName != null) {
						List<String> handlers = ruleName.getExceptionAndHandlers().get(excecaoLancadaPeloMetodo);

						if (getLog() != null) {
							getLog().log(new Status(Status.WARNING, PLUGIN_LOG_IDENTIFIER, "WARNING - Handling information detected (PossibleHandlersInformation). Rule: " + ruleName + 
									" / Class: " + method.getAstRep().getTypeDeclaration().getName().toString() + 
									" / Method: " + method.getMethodDeclaration().getName().toString() +
									" / Exception: " + excecaoLancadaPeloMetodo));
						}

						ReturnMessage rm = new ReturnMessage();
						rm.setMessage("Should be caught by (Policy rule "+ ruleName.getId() + "): " + formatHandler(handlers));
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
