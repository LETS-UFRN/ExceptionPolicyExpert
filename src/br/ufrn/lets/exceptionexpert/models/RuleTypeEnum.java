package br.ufrn.lets.exceptionexpert.models;

/**
 * Possible types of Rule (full or partial)
 * @author taiza
 *
 */
public enum RuleTypeEnum {

	FULL ("full"),
	PARTIAL ("partial");
	
	private String type;

	RuleTypeEnum (String type) {
		this.setType(type);
	}
	
	public boolean isFull() {
		return this == RuleTypeEnum.FULL;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
