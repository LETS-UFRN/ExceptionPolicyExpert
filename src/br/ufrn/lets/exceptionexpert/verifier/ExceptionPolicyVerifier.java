package br.ufrn.lets.exceptionexpert.verifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.ILog;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.MethodRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.Rule;
import br.ufrn.lets.exceptionexpert.models.RulesRepository;

/**
 * Superclass that concentrates all methods of verifiers
 */
public abstract class ExceptionPolicyVerifier {
	
	protected final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
			
	/**
	 * Abstract Syntax Tree representation of target class
	 */
	ASTExceptionRepresentation astRep;
	
	private ILog log;
	
	public ExceptionPolicyVerifier(ASTExceptionRepresentation astRep, ILog log) {
		super();
		this.astRep = astRep;
		this.log = log;
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
	 * Return all methods that throw exception and that there is a full rule related to it
	 * @return Map whose key is the method and the value is a list of rules related to it
	 */
	protected Map<MethodRepresentation, List<Rule>> getMapMethodsAndRulesRelatedToSignaler(boolean typeFullRulesOnly) {
		Map<MethodRepresentation, List<Rule>> map = new HashMap<MethodRepresentation, List<Rule>>();
		
		List<Rule> signalersWildcardAll = new ArrayList<Rule>();
		List<Rule> signalersDeterminedClass = new ArrayList<Rule>();
		List<Rule>signalersDeterminedMethod  = new ArrayList<Rule>();
		List<Rule>signalersDeterminedPackage  = new ArrayList<Rule>();

		if (typeFullRulesOnly) {
			signalersWildcardAll = RulesRepository.getSignalersWildcardAllTypeFull();
			signalersDeterminedClass = RulesRepository.getSignalersDeterminedClassTypeFull();
			signalersDeterminedMethod = RulesRepository.getSignalersDeterminedMethodTypeFull();
			signalersDeterminedPackage = RulesRepository.getSignalersDeterminedPackageTypeFull();

		} else {
			signalersWildcardAll = RulesRepository.getSignalersWildcardAll();
			signalersDeterminedClass = RulesRepository.getSignalersDeterminedClass();
			signalersDeterminedMethod = RulesRepository.getSignalersDeterminedMethod();
			signalersDeterminedPackage = RulesRepository.getSignalersDeterminedPackage();

		}
		
		
		for (MethodRepresentation method : astRep.getMethods()) {
			List<Rule> rules = new LinkedList<Rule>();

			if (method.getThrowStatements() != null && method.getThrowStatements().size() > 0) {
				//If the method throws some exception
				
				if (signalersWildcardAll != null) {
					//Add all the rules, because they have a wildcard (*) as signaler
					rules.addAll(signalersWildcardAll);
				}
				
				if (signalersDeterminedClass != null) {
					//Add all the rules that its signaler matches this actual class
					
					for (Rule rule : signalersDeterminedClass) {
						if (nameSignalerAndClassNameMatche(rule)) {
							rules.add(rule);
						}
					}
					
				}
				
				if (signalersDeterminedMethod != null) {
					//Add all the rules that its signaler matches this actual method
					for (Rule rule : signalersDeterminedMethod) {
						if (methodSignalersDeterminedMethodMatches(rule, method)) {
							rules.add(rule);
						}
					}
				}
				
				if (signalersDeterminedPackage != null) {
					//Add all the rules that its signaler matches this actual package
					for (Rule rule : signalersDeterminedPackage) {
						if (signalerPackageAndPackageNameMatche(rule, method)) {
							rules.add(rule);
						}
					}
				}
				
				map.put(method, rules);
			}
			
		}
		
		return map;
	}
	
	/**
	 * Verifies if the method name of the signaler of the rule matches with the method name of the changed class
	 * @param rule
	 * @param method
	 * @return
	 */
	private boolean methodSignalersDeterminedMethodMatches(Rule rule, MethodRepresentation method) {
		if (nameSignalerAndClassNameMatche(rule)) {
			//Verify if the name of method match
			
			String signaler = rule.getSignaler();
			signaler = signaler.replace("(..)", "");
			String[] split = signaler.split("\\.");
			String nameOfSignalerMethod = split[split.length-1];
			String nameOfChangedMethod = method.getMethodDeclaration().getName().toString();
			
			return nameOfSignalerMethod.compareTo(nameOfChangedMethod) == 0;
		}
		return false;
	}
	
	/**
	 * Verifies if the package/class name of the signaler of the rule matches with the package/class name of the changed class.
	 * The signaler should be in one of this formats:
	 * - p1.p2.ClassName.*
	 * - ClassName.*
	 * - p1.p2.ClassName.methodName(..)
	 * - ClassName.methodName(..)
	 * @param rule
	 * @return
	 */
	private boolean nameSignalerAndClassNameMatche(Rule rule) {
		String signaler = rule.getSignaler();
		
		signaler = signaler.replace("(..)", "");
		String[] split = signaler.split("\\.");
		
		boolean hasPackage = split.length > 2;
		
		
		String nameOfSignalerClass = split[split.length-2]; //The last element will be the * character or the name of method
		
		String nameOfChangedClass = getAstRep().getTypeDeclaration().getName().toString();
		
		if (nameOfSignalerClass.compareTo(nameOfChangedClass) == 0) {
			if (!hasPackage) {
				return true;
			} else {
				//Verify if the packages
				String nameOfSiglanerPackage = "";
				for (int i = 0; i < split.length - 2; i++) {
					nameOfSiglanerPackage += split[i] + ".";
				}
				nameOfSiglanerPackage = nameOfSiglanerPackage.substring(0, nameOfSiglanerPackage.length()-1); //Removes the last "."
				String nameOfChangedClassPackage = getAstRep().getPackageDeclaration().getName().toString();
				
				return nameOfSiglanerPackage.compareTo(nameOfChangedClassPackage) == 0;
			}
		}
		return false;
	}
	
	/**
	 * Verifies if the method name of the package of signaler matches with the method name of the package of changed class
	 * @param rule
	 * @param method
	 * @return
	 */
	private boolean signalerPackageAndPackageNameMatche(Rule rule, MethodRepresentation method) {
		PackageDeclaration classPackage = method.getAstRep().getPackageDeclaration();
		
		if (rule.isSignalerPackageDAO() && classPackage.getName().toString().contains("dao"))
			return true;
		
		if (rule.isSignalerPackageView() && classPackage.getName().toString().contains("jsf"))
			return true;
		
		return false;
	}
	
	public ASTExceptionRepresentation getAstRep() {
		return astRep;
	}

	public void setAstRep(ASTExceptionRepresentation astRep) {
		this.astRep = astRep;
	}

	public ILog getLog() {
		return log;
	}

	public void setLog(ILog log) {
		this.log = log;
	}
	
}
