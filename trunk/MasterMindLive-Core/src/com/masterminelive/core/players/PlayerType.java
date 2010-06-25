package com.masterminelive.core.players;

public class PlayerType {

	public static final String TYPE_CD_HUMAN = "HUMAN";
	public static final String TYPE_CD_COMPUTER = "COMPUTER";
	public static final String ROLE_CD_SECRET_CREATOR = "SECRET_CREATOR";
	public static final String ROLE_CD_GUESSER = "GUESSER";
	
	private String typeCd;
	private String desc;
	private String roleCd;
	
	public String getTypeCd() {
		return typeCd;
	}
	public void setTypeCd(String typeCd) {
		this.typeCd = typeCd;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getRoleCd() {
		return roleCd;
	}
	public void setRoleCd(String roleCd) {
		this.roleCd = roleCd;
	}
	
}
