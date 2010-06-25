package com.masterminelive.core.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.masterminelive.core.players.Player;
import com.masterminelive.util.OperationResponse;

public class MasterMindGame {

	public static Integer MAX_ALLOWED_GUESSES = 20;
	public static Integer MAX_ALLOWED_SPOTS = 10;
	public static Integer MAX_ALLOWED_COLORS = 10;
	
	private Integer slotSeq;
	private Integer numGuesses;
	private Integer numSpots;
	private Integer numColors;
	private Player player1;
	private Player player2;
	private Map<Integer, Slot> slots;
	private List<Piece> guessOptions;
	private Integer currentGuessSlotNum;
	private Slot secretSlot;
	private Boolean secretSlotFinalized;
	
	public MasterMindGame(Integer argNumSpots, Integer argNumGuesses, Integer argNumColors, Player argPlayer1, Player argPlayer2){
		if(argNumSpots == null)
			throw new IllegalArgumentException("argNumSpots is null");
		
		if(argNumSpots < 1 || argNumSpots > MAX_ALLOWED_SPOTS)
			throw new IllegalArgumentException("argNumSpots is not within allowed limit of 1-" + MAX_ALLOWED_SPOTS);
		
		if(argNumGuesses == null)
			throw new IllegalArgumentException("argNumGuesses is null");
		
		if(argNumGuesses < 1 || argNumGuesses > MAX_ALLOWED_GUESSES)
			throw new IllegalArgumentException("argNumGuesses is not within allowed limit of 1-" + MAX_ALLOWED_GUESSES);
		
		if(argNumColors == null)
			throw new IllegalArgumentException("argNumColors is null");
		
		if(argNumColors < 1 || argNumColors > MAX_ALLOWED_COLORS)
			throw new IllegalArgumentException("argNumColors is not within allowed limit of 1-" + MAX_ALLOWED_COLORS);
		
		if(argPlayer1 == null)
			throw new IllegalArgumentException("argPlayer1 is null");
		
		if(argPlayer2 == null)
			throw new IllegalArgumentException("argPlayer2 is null");
		
		this.numSpots = argNumSpots;
		this.numGuesses = argNumGuesses;
		this.numColors = argNumColors;
		this.player1 = argPlayer1;
		this.player2 = argPlayer2;
		this.slotSeq = 1;
		this.currentGuessSlotNum = 1;
		
		this.slots = new HashMap<Integer, Slot>();
		for(int i=1; i <= this.numGuesses; i++){
			Slot slot = new Slot(this, getAndIncrementSlotSeq());
			this.slots.put(slot.getSlotNum(), slot);
		}
		
		
		this.secretSlot = new Slot(this, this.slotSeq + 1);
		this.secretSlotFinalized = false;
		
		this.guessOptions = new ArrayList<Piece>();
		for(int i=1; i <= this.numColors; i++){
			Piece piece = new Piece();
			switch (i){
				case 1: piece.setColor(Piece.COLOR_RED); break;
				case 2: piece.setColor(Piece.COLOR_WHITE); break;
				case 3: piece.setColor(Piece.COLOR_BLUE); break;
				case 4: piece.setColor(Piece.COLOR_GREEN); break;
				case 5: piece.setColor(Piece.COLOR_BLACK); break;
				case 6: piece.setColor(Piece.COLOR_YELLOW); break;
				case 7: piece.setColor(Piece.COLOR_ORANGE); break;
				case 8: piece.setColor(Piece.COLOR_PURPLE); break;
				case 9: piece.setColor(Piece.COLOR_PINK); break;
				case 10: piece.setColor(Piece.COLOR_BROWN); break;
			}
			
			this.guessOptions.add(piece);
		}
	}
	
	public Slot getCurrentSlot(){
		return this.slots.get(this.currentGuessSlotNum);
	}
	
	public void addSecretPiece(Integer slotSpotNum, Piece secretPiece){
		if(slotSpotNum == null)
			throw new IllegalArgumentException("slotSpotNum is null");
		
		if(slotSpotNum < 1 || slotSpotNum > this.numSpots)
			throw new IllegalArgumentException("slotSpotNum is not within valid range of 1-" + this.numSpots);
		
		if(secretPiece == null || secretPiece.hasColor() == false)
			throw new IllegalArgumentException("secretPiece is null or does not have color set");
		
		/*
		 * Note we allow piece to overlay existing piece if one happen to exist
		*/
		SlotSpot slotSpot = this.secretSlot.getSpots().get(slotSpotNum);
		if(slotSpot == null)
			throw new IllegalStateException("Got null slotSpot for slotSpotNum " + slotSpotNum);
		
		slotSpot.setPiece(secretPiece);		
	}
	
	public OperationResponse finalizeSecretSlot(){
		OperationResponse resp = new OperationResponse();
		if(this.secretSlot.allSlotSpotsFilled() == false){
			resp.setErrorMsg("Please  a color for all the spots.");
			return resp;
		}
		
		this.secretSlotFinalized = true;		
		return resp;
	}
	
	public void addGuessPiece(Integer slotSpotNum, Piece guessPiece){
		if(slotSpotNum == null)
			throw new IllegalArgumentException("slotSpotNum is null");
		
		if(slotSpotNum < 1 || slotSpotNum > this.numSpots)
			throw new IllegalArgumentException("slotSpotNum is not within valid range of 1-" + this.numSpots);
		
		if(guessPiece == null || guessPiece.hasColor() == false)
			throw new IllegalArgumentException("guessPiece is null or does not have color set");
		
		/*
		 * Note we allow piece to overlay existing piece if one happen to exist
		*/
		SlotSpot slotSpot = getCurrentSlot().getSpots().get(slotSpotNum);
		if(slotSpot == null)
			throw new IllegalStateException("Got null slotSpot for slotSpotNum " + slotSpotNum);
		
		slotSpot.setPiece(guessPiece);		
	}
	
	
	public OperationResponse guess(){
		OperationResponse resp = new OperationResponse();
		Slot slot = getCurrentSlot();
		if(slot.allSlotSpotsFilled() == false){
			resp.setErrorMsg("Please guess a color for all the spots");
			return resp;
		}
		
		this.getCurrentSlot().setGuessEntered(true);
		this.currentGuessSlotNum++;
		
		return resp;
	}
	
	private Integer getAndIncrementSlotSeq(){
		if(this.slotSeq > MAX_ALLOWED_GUESSES)
			throw new IllegalStateException("Cannot give slot seq, max slots allowed of " + MAX_ALLOWED_GUESSES + " already reached.");
		Integer currentSlotSeq = new Integer(this.slotSeq);
		this.slotSeq++;
		return currentSlotSeq;
	}

	public Integer getNumGuesses() {
		return numGuesses;
	}

	public Integer getNumSpots() {
		return numSpots;
	}

	public Integer getNumColors() {
		return numColors;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}
	
	
}
