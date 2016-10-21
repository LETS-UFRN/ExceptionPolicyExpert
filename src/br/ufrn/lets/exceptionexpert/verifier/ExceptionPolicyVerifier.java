package br.ufrn.lets.exceptionexpert.verifier;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.Rule;
import br.ufrn.lets.exceptionexpert.models.RulesRepository;

/**
 * Superclass that concentrates all methods of verifiers
 */
public abstract class ExceptionPolicyVerifier {
	
	/**
	 * Abstract Syntax Tree representation of target class
	 */
	ASTExceptionRepresentation astRep;
	
	public ExceptionPolicyVerifier(ASTExceptionRepresentation astRep) {
		super();
		this.astRep = astRep;
	}

	/**
	 * Implements the precondition to the verification
	 * @return
	 */
	protected abstract boolean preCondition();
	
	/**
	 * Implements the verification algorithm
	 * @return
	 */
	protected abstract List<ReturnMessage> verify();
	
	/**
	 * Return a list containing only the rules of type "full"
	 * @return
	 */
	protected List<Rule> getOnlyFullRules() {
		
		//TODO maybe is not necessary
		
		List<Rule> fullRules = new ArrayList<Rule>();
		
		for (Rule rule : RulesRepository.getRules()) {
			if (rule.isFull()) {
				fullRules.add(rule);
			}
		}
		return fullRules;
	}
	
	public ASTExceptionRepresentation getAstRep() {
		return astRep;
	}

	public void setAstRep(ASTExceptionRepresentation astRep) {
		this.astRep = astRep;
	}
	
}
