package br.ufrn.lets.exceptionexpert.models;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Rule {

	String type;
	
	String signaler;
	
	private Map<String, List<String>> exceptionAndHandlers;
	
	
	public Rule() {
		super();
	}

	public Rule(String type, String signaler) {
		super();
		this.type = type;
		this.signaler = signaler;
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
}
