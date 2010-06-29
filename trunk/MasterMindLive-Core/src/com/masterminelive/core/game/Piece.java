package com.masterminelive.core.game;

public class Piece {

	//Since a piece must be one and only one color, use HEX color integers
	//as unique identifiers
	public static final int COLOR_RED = 0xFFFF0000;
	public static final int COLOR_BLUE = 0xFF0000FF;
	public static final int COLOR_GREEN = 0xFF66FF66;
	public static final int COLOR_YELLOW = 0xFFFFFF33;
	public static final int COLOR_BLACK = 0x00000000;
	public static final int COLOR_ORANGE = 0xFFFF9933;
	public static final int COLOR_PURPLE = 0xFFFF00CC;
	
	private int color;
	
	public Piece(Piece argAnother){
		this.color = argAnother.color;
	}
	public Piece(int argColor){
		this.color = argColor;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
}
