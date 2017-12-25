package br.ufrn.lets.exceptionexpert.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.CatchClause;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.MethodRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.Rule;
import br.ufrn.lets.exceptionexpert.models.RulesRepository;

/**
 * This class verifies if a method handles an exception improperly. 
 * This happens when there is a rule that determines that the signaler "cannotHandle" this exception.
 *
 * This is the second proposed verification.
 */
public class ImproperHandlingVerifier extends ExceptionPolicyVerifier {
	
	private static final String PLUGIN_LOG_IDENTIFIER = "br.ufrn.lets.exceptionExpert";

	public ImproperHandlingVerifier(ASTExceptionRepresentation astRep, ILog log) {
		super(astRep, log);
	}

	@Override
	protected boolean preCondition() {
		//Verifies if the target class has an exception catch
		return astRep != null && astRep.hasCatchStatements();
	}
	
	/**
	 * Return the rule name that contains an exception that does match with the exception that the verified method throws, because this is a violation.  
	 * @param method 
	 * @param excecaoCapturadaPeloMetodo
	 * @param rules
	 * @return
	 */
	private Rule getRuleNameMatchWithMethodException(MethodRepresentation method, CatchClause excecaoCapturadaPeloMetodo, List<Rule> rules) {

		for (Rule rule : rules) {
			Map<String, List<String>> exceptionAndCannotHandle = rule.getExceptionAndCannotHandle();
			
			Set<String> exceptions = exceptionAndCannotHandle.keySet();
			
			
			for (String ruleException : exceptions) {
				if (ruleException.compareTo(excecaoCapturadaPeloMetodo.getException().getType().toString()) == 0) {
					return rule;
				}
			}
		}
		return null;
	}
	
	@Override
	public List<ReturnMessage> verify() {
		
		List<ReturnMessage> returnM = new ArrayList<ReturnMessage>();

		if (preCondition()) {

			//Get all rules with signaler = "*"
			List<Rule> rulesWithCannot = new ArrayList<>();
			List<Rule> signalersWildcardAll = RulesRepository.getSignalersWildcardAll();
			
			//GEt all rules that have cannot-handler
			for (Rule r : signalersWildcardAll) {
				if (r.getExceptionAndCannotHandle() != null && r.getExceptionAndCannotHandle().size() > 0)
					rulesWithCannot.add(r);
			} 
			
			if (rulesWithCannot.size() > 0) {
				
				for (MethodRepresentation method : astRep.getMethods()) {
					
					List<CatchClause> methodCatchesStatements = method.getCatchClauses();

					for(CatchClause catchClause : methodCatchesStatements) {
						
						//Get rules related to exception
						Rule ruleName = getRuleNameMatchWithMethodException(method, catchClause, rulesWithCannot);
						
						if (ruleName != null) {

							if (getLog() != null) {
								getLog().log(new Status(Status.WARNING, PLUGIN_LOG_IDENTIFIER, "WARNING - Violation detected (ImproperHandlingVerifier). Rule: " + ruleName + 
										" / Class: " + method.getAstRep().getTypeDeclaration().getName().toString() + 
										" / Method: " + method.getMethodDeclaration().getName().toString() +
										" / Catched Exception: " + catchClause.getException().getType().toString()));
							}
							
							ReturnMessage rm = new ReturnMessage();
							rm.setMessage("VIOLATION: should not be catching the exception " + catchClause.getException().getType().toString() + " (Policy rule " + ruleName + ")");
							rm.setLineNumber(getAstRep().getAstRoot().getLineNumber(catchClause.getStartPosition()));
							rm.setMarkerSeverity(IMarker.SEVERITY_WARNING);
							returnM.add(rm);
						}
							
					}
					
				}
				
			}
			
			
			
		}

		return returnM;
	}

}
