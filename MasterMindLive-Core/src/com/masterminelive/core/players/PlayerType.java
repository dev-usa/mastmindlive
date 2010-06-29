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
	
	public static boolean isPlayerComputer(Player player){
		if(player== null)
			throw new IllegalArgumentException("player is null");
		
		if(player.getPlayerType()== null)
			throw new IllegalArgumentException("player type is null");
		
		if(player.getPlayerType().getTypeCd().compareToIgnoreCase(TYPE_CD_COMPUTER)==0)
			return true;
		else
			return false;
	}
}
