package br.ufrn.lets.exceptionexpert.models;

import java.util.ArrayList;
import java.util.List;

public class RulesRepository {

	private static List<Rule> rules;

	private static List<Rule> signalersWildcardAll;

	private static List<Rule> signalersWildcardAllTypeFull;

	private static List<Rule> signalersDeterminedClass;

	private static List<Rule> signalersDeterminedClassTypeFull;

	private static List<Rule> signalersDeterminedMethod;

	private static List<Rule> signalersDeterminedMethodTypeFull;

	public static List<Rule> getRules() {
		return rules;
	}

	public static void setRules(List<Rule> rules) {
		RulesRepository.rules = rules;
		
		processRules();
	}

	private static void processRules() {
		// TODO Auto-generated method stub
		
		signalersWildcardAll = new ArrayList<Rule>();
		signalersDeterminedClass = new ArrayList<Rule>();
		signalersDeterminedMethod = new ArrayList<Rule>();

		signalersWildcardAllTypeFull = new ArrayList<Rule>();
		signalersDeterminedClassTypeFull = new ArrayList<Rule>();
		signalersDeterminedMethodTypeFull = new ArrayList<Rule>();

		for (Rule rule : getRules()) {
			
			if (rule.getSignalerPattern().compareTo(RuleElementPattern.ASTERISC_WILDCARD) == 0) {
				signalersWildcardAll.add(rule);
				
				if (rule.isFull())
					signalersWildcardAllTypeFull.add(rule);
				
			} else if (rule.getSignalerPattern().compareTo(RuleElementPattern.CLASS_DEFINITION) == 0) {
				signalersDeterminedClass.add(rule);
				
				if (rule.isFull())
					getSignalersDeterminedClassTypeFull().add(rule);

			} else if (rule.getSignalerPattern().compareTo(RuleElementPattern.METHOD_DEFINITION) == 0) {
				signalersDeterminedMethod.add(rule);
				
				if (rule.isFull())
					getSignalersDeterminedMethodTypeFull().add(rule);

			}
			
		}
		
	}

	public static List<Rule> getSignalersWildcardAll() {
		return signalersWildcardAll;
	}

	public static List<Rule> getSignalersDeterminedClass() {
		return signalersDeterminedClass;
	}

	public static List<Rule> getSignalersDeterminedMethod() {
		return signalersDeterminedMethod;
	}

	public static List<Rule> getSignalersWildcardAllTypeFull() {
		return signalersWildcardAllTypeFull;
	}

	public static List<Rule> getSignalersDeterminedClassTypeFull() {
		return signalersDeterminedClassTypeFull;
	}

	public static List<Rule> getSignalersDeterminedMethodTypeFull() {
		return signalersDeterminedMethodTypeFull;
	}

	
}
