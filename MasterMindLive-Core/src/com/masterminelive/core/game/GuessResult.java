package com.masterminelive.core.game;

import java.util.ArrayList;
import java.util.List;

public class GuessResult {

	private List<GuessResultPart> guessResultParts;
	private Slot slot;
	private boolean winningGuess = false;
	
	public GuessResult(Slot argSlot){
		if(argSlot == null)
			throw new IllegalArgumentException("argSlot is null");
		
		this.slot = argSlot;
		this.guessResultParts = new ArrayList<GuessResultPart>();
	}
	
	public List<GuessResultPart> getGuessResultParts() {
		return guessResultParts;
	}

	public void addGuesResult(GuessResultPart guestResult){
		this.guessResultParts.add(guestResult);
		evaluateWinningGuess();
	}
	
	private void evaluateWinningGuess(){
		int rightColorRightSpotCounter = 0;
		int colorSpots = this.slot.getGame().getNumSpots();
		
		for(GuessResultPart grp : this.guessResultParts){
			if(grp.isRightColorRightSpot())
				rightColorRightSpotCounter++;
		}
		
		if(colorSpots == rightColorRightSpotCounter)
			this.winningGuess = true;
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

	public Boolean getWinningGuess() {
		return winningGuess;
	}
	
	
}
