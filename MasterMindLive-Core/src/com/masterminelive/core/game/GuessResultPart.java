package com.masterminelive.core.game;

public class GuessResultPart {

	public static final int GUESS_RESULT_RIGHT_COLOR_RIGHT_POSITION = 1;
	public static final int GUESS_RESULT_RIGHT_COLOR_WRONG_POSITION = 2;
	
	private int guessReultValue;

	public GuessResultPart(int argGuessResultValue){
		if(argGuessResultValue == 0)
			throw new IllegalArgumentException("argGuessResultValue is not valid");
		
		this.guessReultValue = argGuessResultValue;
	}
	
	public int getGuessReultValue() {
		return guessReultValue;
	}
	
}
