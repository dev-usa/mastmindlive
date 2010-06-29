package com.masterminelive.core.game;

import java.util.ArrayList;
import java.util.List;

public class GuessResult {

	List<GuessResultPart> guessResultParts;
	
	public GuessResult(){
		this.guessResultParts = new ArrayList<GuessResultPart>();
	}
	
	public List<GuessResultPart> getGuessResultParts() {
		return guessResultParts;
	}

	public void addGuesResult(GuessResultPart guestResult){
		this.guessResultParts.add(guestResult);
	}
	
	public String getResultDesc(){
		int numRightColorRightSpot = 0;
		int numRightColorWrongSpot = 0;
		for(GuessResultPart part : this.guessResultParts){
			if(part.getGuessReultValue() == GuessResultPart.GUESS_RESULT_RIGHT_COLOR_RIGHT_POSITION)
				numRightColorRightSpot++;
			
			if(part.getGuessReultValue() == GuessResultPart.GUESS_RESULT_RIGHT_COLOR_WRONG_POSITION)
				numRightColorWrongSpot++;
		}
		
		return "Result: " + numRightColorRightSpot + " black," + numRightColorWrongSpot + " white";
	}
}
