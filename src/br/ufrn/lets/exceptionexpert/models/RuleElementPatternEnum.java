package br.ufrn.lets.exceptionexpert.models;

/**
 * The way the element (signaler, handler or exception) is described, in terms of wildcards
 * @author taiza
 *
 */
public enum RuleElementPatternEnum {

	ASTERISC_WILDCARD, //*
	CLASS_DEFINITION, //for example: p1.p2.Class.*
	METHOD_DEFINITION, //for example: p1.p2.Class.method1(..)
	PACKAGE_DEFINITION; //for example: *.dao.*
	
}
