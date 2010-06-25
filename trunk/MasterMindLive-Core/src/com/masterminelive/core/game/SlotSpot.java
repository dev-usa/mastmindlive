package com.masterminelive.core.game;

public class SlotSpot {

	private Piece piece;

	public Boolean isEmpty(){
		if(piece == null)
			return true;
		else
			return false;
		
	}
	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	
}
