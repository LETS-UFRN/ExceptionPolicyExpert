package br.ufrn.lets.exceptionexpert.models;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents a ECL rule
 * @author taiza
 *
 */
public class Rule {

	/**
	 * Type of the rule (full or partial)
	 */
	String type;
	
	/**
	 * Signaler of the rule
	 */
	String signaler;
	
	/**
	 * Signaler type (the way the signaler is defined)
	 */
	private RuleElementPattern signalerPattern;
	
	/**
	 * Map with the element exception and its handlers
	 */
	private Map<String, List<String>> exceptionAndHandlers;
	
	
	public Rule() {
		super();
	}

	public Rule(String type, String signaler) {
		super();
		this.type = type;
		this.signaler = signaler;
	}
	
	public boolean isFull() {
		//TODO do a Enum to list the kind of rules
		return getType().equals("full");
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
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
		StringBuilder toString = new StringBuilder();
		toString.append("Tipo " + getType());
		toString.append("signaler " + getSignaler());
		
		Set<Entry<String, List<String>>> entrySet = getExceptionAndHandlers().entrySet();
		Iterator<Entry<String, List<String>>> iterator = entrySet.iterator();
		while(iterator.hasNext()) {
			Entry<String, List<String>> next = iterator.next();
			toString.append("Exception " + next.getKey());
			for (String s : next.getValue()) {
				toString.append("     Handler " + s);
			}
		}
		
		return toString.toString();
	}

	public RuleElementPattern getSignalerPattern() {
		return signalerPattern;
	}

	public void setSignalerPattern(RuleElementPattern signalerPattern) {
		this.signalerPattern = signalerPattern;
	}
}
