package com.masterminelive.core.game;

public class Piece {

	public static final String COLOR_RED = "RED";
	public static final String COLOR_BLUE = "BLUE";
	public static final String COLOR_GREEN = "GREEN";
	public static final String COLOR_YELLOW = "YELLOW";
	public static final String COLOR_BLACK = "BLACK";
	public static final String COLOR_WHITE = "WHITE";
	public static final String COLOR_BROWN = "BROWN";
	public static final String COLOR_ORANGE = "ORANGE";
	public static final String COLOR_PURPLE = "PURPLE";
	public static final String COLOR_PINK = "PINK";
	
	private String color;
	
	public Boolean hasColor(){
		if(color == null)
			return false;
		else
			return true;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	
}
