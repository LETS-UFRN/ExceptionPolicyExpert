package br.ufrn.lets.exceptionexpert.models;

import java.util.List;
import java.util.Map;

/**
 * Represents a ECL rule
 * @author taiza
 *
 */
public class Rule {
	
	/**
	 * Id o the rule
	 */
	private String id;

	/**
	 * Type of the rule (full or partial)
	 */
	private RuleTypeEnum type;
	
	/**
	 * Signaler of the rule
	 */
	private String signaler;
	
	/**
	 * Signaler type (the way the signaler is defined)
	 */
	private RuleElementPatternEnum signalerPattern;
	
	/**
	 * Map with the element exception and its handlers
	 */
	private Map<String, List<String>> exceptionAndHandlers;
	
	public Rule() {
		super();
	}

	public Rule(RuleTypeEnum type, String signaler) {
		super();
		this.type = type;
		this.signaler = signaler;
	}
	
	public boolean isFull() {
		return getType().isFull();
	}
	
	public RuleTypeEnum getType() {
		return type;
	}
	
	public void setType(RuleTypeEnum type) {
		this.type = type;
	}
	
	public String getSignaler() {
		return signaler;
	}
	
	public void setSignaler(String signaler) {
		this.signaler = signaler;
	}

	public Map<String, List<String>> getExceptionAndHandlers() {
		return exceptionAndHandlers;
	}

	public void setExceptionAndHandlers(Map<String, List<String>> exceptionAndHandlers) {
		this.exceptionAndHandlers = exceptionAndHandlers;
	}
	
	@Override
	public String toString() {
		return getId();
	}

	public RuleElementPatternEnum getSignalerPattern() {
		return signalerPattern;
	}

	public void setSignalerPattern(RuleElementPatternEnum signalerPattern) {
		this.signalerPattern = signalerPattern;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
