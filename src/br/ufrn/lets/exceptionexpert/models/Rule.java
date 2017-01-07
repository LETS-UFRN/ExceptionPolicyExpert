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
	 * If true, the signaler is the package DAO (all classes)
	 */
	private boolean signalerPackageDAO;
	
	/**
	 * If true, the signaler is the package View (all classes)
	 */
	private boolean signalerPackageView;
	
	/**
	 * Signaler type (the way the signaler is defined)
	 */
	private RuleElementPatternEnum signalerPattern;
	
	/**
	 * Map with the element exception and its handlers
	 */
	private Map<String, List<String>> exceptionAndHandlers;

	/**
	 * Map with the element exception and elements that cannot handle the exception
	 */
	private Map<String, List<String>> exceptionAndCannotHandle;

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

	public Map<String, List<String>> getExceptionAndCannotHandle() {
		return exceptionAndCannotHandle;
	}

	public void setExceptionAndCannotHandle(Map<String, List<String>> exceptionAndCannotHandle) {
		this.exceptionAndCannotHandle = exceptionAndCannotHandle;
	}

	public boolean isSignalerPackageDAO() {
		return signalerPackageDAO;
	}

	public void setSignalerPackageDAO(boolean signalerPackageDAO) {
		this.signalerPackageDAO = signalerPackageDAO;
	}

	public boolean isSignalerPackageView() {
		return signalerPackageView;
	}

	public void setSignalerPackageView(boolean signalerPackageView) {
		this.signalerPackageView = signalerPackageView;
	}
}
