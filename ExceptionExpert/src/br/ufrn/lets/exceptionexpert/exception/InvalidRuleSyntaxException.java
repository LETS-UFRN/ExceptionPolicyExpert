package br.ufrn.lets.exceptionexpert.exception;

/**
 * Exception to indicate a ECL Rule with invalid syntax
 * @author Taiza Montenegro
 *
 * 22/10/2016
 *
 */
public class InvalidRuleSyntaxException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidRuleSyntaxException(String msg) {
		super(msg);
	}
}
