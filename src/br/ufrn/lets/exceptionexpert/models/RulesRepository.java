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

	private static List<Rule> signalersDeterminedPackage;

	private static List<Rule> signalersDeterminedPackageTypeFull;

	public static List<Rule> getRules() {
		return rules;
	}

	public static void setRules(List<Rule> rules) {
		RulesRepository.rules = rules;
		
		processRules();
	}

	private static void processRules() {
		signalersWildcardAll = new ArrayList<Rule>();
		signalersDeterminedClass = new ArrayList<Rule>();
		signalersDeterminedMethod = new ArrayList<Rule>();
		signalersDeterminedPackage = new ArrayList<Rule>();

		signalersWildcardAllTypeFull = new ArrayList<Rule>();
		signalersDeterminedClassTypeFull = new ArrayList<Rule>();
		signalersDeterminedMethodTypeFull = new ArrayList<Rule>();
		signalersDeterminedPackageTypeFull = new ArrayList<Rule>();

		for (Rule rule : getRules()) {
			
			if (rule.getSignalerPattern().compareTo(RuleElementPatternEnum.ASTERISC_WILDCARD) == 0) {
				signalersWildcardAll.add(rule);
				
				if (rule.isFull())
					signalersWildcardAllTypeFull.add(rule);
				
			} else if (rule.getSignalerPattern().compareTo(RuleElementPatternEnum.CLASS_DEFINITION) == 0) {
				signalersDeterminedClass.add(rule);
				
				if (rule.isFull())
					getSignalersDeterminedClassTypeFull().add(rule);

			} else if (rule.getSignalerPattern().compareTo(RuleElementPatternEnum.METHOD_DEFINITION) == 0) {
				signalersDeterminedMethod.add(rule);
				
				if (rule.isFull())
					getSignalersDeterminedMethodTypeFull().add(rule);

			} else if (rule.getSignalerPattern().compareTo(RuleElementPatternEnum.PACKAGE_DEFINITION) == 0) {
				signalersDeterminedPackage.add(rule);
				
				if (rule.isFull())
					getSignalersDeterminedPackageTypeFull().add(rule);

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

	public static List<Rule> getSignalersDeterminedPackage() {
		return signalersDeterminedPackage;
	}

	public static void setSignalersDeterminedPackage(List<Rule> signalersDeterminedPackage) {
		RulesRepository.signalersDeterminedPackage = signalersDeterminedPackage;
	}

	public static List<Rule> getSignalersDeterminedPackageTypeFull() {
		return signalersDeterminedPackageTypeFull;
	}

	public static void setSignalersDeterminedPackageTypeFull(List<Rule> signalersDeterminedPackageTypeFull) {
		RulesRepository.signalersDeterminedPackageTypeFull = signalersDeterminedPackageTypeFull;
	}

	
}
