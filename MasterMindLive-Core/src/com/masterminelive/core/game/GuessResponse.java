package com.masterminelive.core.game;

import com.masterminelive.util.OperationResponse;

public class GuessResponse extends OperationResponse {

	private Boolean winningGuess;

	public Boolean getWinningGuess() {
		return winningGuess;
	}

	public void setWinningGuess(Boolean winningGuess) {
		this.winningGuess = winningGuess;
	}
	
	
}
