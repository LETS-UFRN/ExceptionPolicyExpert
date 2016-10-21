package br.ufrn.lets.exceptionexpert.verifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.lets.exceptionexpert.models.ASTExceptionRepresentation;
import br.ufrn.lets.exceptionexpert.models.MethodRepresentation;
import br.ufrn.lets.exceptionexpert.models.ReturnMessage;
import br.ufrn.lets.exceptionexpert.models.Rule;
import br.ufrn.lets.exceptionexpert.models.RulesRepository;

/**
 * This class verifies if a method throws an exception improperly. 
 * This happens when there are a rule of "full type" that corresponds to the signaler element. 
 * And this signaler throws an exception that is not list on the rule element "exceptionAndHandlers".
 *
 * This is the first proposed verification.
 */
public class ImproperThrowingVerifier extends ExceptionPolicyVerifier {
	
	public ImproperThrowingVerifier(ASTExceptionRepresentation astRep) {
		super(astRep);
	}

	@Override
	protected boolean preCondition() {
		//Verifies if the target class has an exception signaler
		return astRep != null && astRep.hasThrowsStatements();
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
	 * Verifies if the package/class name of the signaler of the rule matches with the package/class name of the changed class
	 * @param rule
	 * @return
	 */
	private boolean nameSignalerAndClassNameMatche(Rule rule) {
		String signaler = rule.getSignaler();
		//The signaler should be in one of this formats:
		//p1.p2.ClassName.*
		//ClassName.*
		//p1.p2.ClassName.methodName(..)
		//ClassName.methodName(..)
		
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
					nameOfSiglanerPackage += split[i];
				}
				String nameOfChangedClassPackage = getAstRep().getPackageDeclaration().getName().toString();
				
				return nameOfSiglanerPackage.compareTo(nameOfChangedClassPackage) == 0;
			}
		}
		return false;
	}
	
	/**
	 * Return all methods that throw exception and that there is a full rule related to it
	 * @return Map whose key is the method and the value is a list of rules related to it
	 */
	private Map<MethodRepresentation, List<Rule>> getMapMethodsAndRulesRelatedToSignaler() {
		Map<MethodRepresentation, List<Rule>> map = new HashMap<MethodRepresentation, List<Rule>>();
		
		List<Rule> signalersWildcardAllTypeFull = RulesRepository.getSignalersWildcardAllTypeFull();
		List<Rule> signalersDeterminedClassTypeFull = RulesRepository.getSignalersDeterminedClassTypeFull();
		List<Rule> signalersDeterminedMethodTypeFull = RulesRepository.getSignalersDeterminedMethodTypeFull();
		
		for (MethodRepresentation method : astRep.getMethods()) {
			List<Rule> rules = new ArrayList<Rule>();

			if (method.getThrowStatements() != null && method.getThrowStatements().size() > 0) {
				//If the method throws some exception
				
				if (signalersWildcardAllTypeFull != null) {
					//Add all the rules, because they haver a wildcard (*) as signaler
					rules.addAll(signalersWildcardAllTypeFull);
				}
				
				if (signalersDeterminedClassTypeFull != null) {
					//Add all the rules that its signaler matches this actual class
					
					for (Rule rule : signalersDeterminedClassTypeFull) {
						if (nameSignalerAndClassNameMatche(rule)) {
							rules.add(rule);
						}
					}
					
				}
				
				if (signalersDeterminedMethodTypeFull != null) {
					//Add all the rules that its signaler matches this actual method
					for (Rule rule : signalersDeterminedMethodTypeFull) {
						if (methodSignalersDeterminedMethodMatches(rule, method)) {
							rules.add(rule);
						}
					}
				}
				
				map.put(method, rules);
			}
			
		}
		
		return map;
	}

	@Override
	public List<ReturnMessage> verify() {
		List<ReturnMessage> returnM = new ArrayList<ReturnMessage>();

		if (preCondition()) {

			Map<MethodRepresentation, List<Rule>> methods = getMapMethodsAndRulesRelatedToSignaler();
			
			System.out.println(methods);
			
			//TODO continuar. Para cada par do mapa, verificar se a excecao q ele lanca bate com a excecao da regra
			
//			for(Entry<ThrowStatement, Expression> throwStatement : astRep.getSignalerRepresentation().getMapThrows().entrySet()) {
//
//				//Verify if some rule is related to exceptions throws by method
//				List<Rule> rulesRelatedToException = getRulesRelatedToException(throwStatement.getKey(), rulesRelatedToSignaler);
//
//				if (!rulesRelatedToException.isEmpty()) {
//					ReturnMessage rm = new ReturnMessage();
//					rm.setMessage("Should be caught by: ");
//
//					for (Rule r: rulesRelatedToException) {
//						//TODO get class name
//						List<String> list = r.getExceptionAndHandlers().get(throwStatement.getKey().getClass().getName());
//						for (String exc : list){
//							rm.setMessage(rm.getMessage() + "-  "+ exc);
//						}
//					}
//					int lineNumber = astRep.getAstRoot().getLineNumber(throwStatement.getKey().getStartPosition());
//					rm.setLineNumber(lineNumber);
//
//					returnM.add(rm);
//				}
//			}
		}

		return returnM;
	}

}
