package com.masterminelive.core.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.masterminelive.core.players.Player;
import com.masterminelive.core.players.PlayerType;
import com.masterminelive.util.OperationResponse;

public class MasterMindGame implements Serializable{

	public static Integer MAX_ALLOWED_GUESSES = 12;
	public static Integer MAX_ALLOWED_SPOTS = 6;
	public static Integer MAX_ALLOWED_COLORS = 7;
	
	private Integer slotSeq;
	private Integer numGuesses;
	private Integer numSpots;
	private Integer numColors;
	private Player player1;
	private Player player2;
	private SortedMap<Integer, Slot> slots;
	private List<Piece> guessOptions;
	private Integer currentGuessSlotNum;
	private Slot secretSlot;
	private Boolean secretSlotFinalized;
	
	/**
	 * 
	 * @param argNumSpots
	 * @param argNumGuesses
	 * @param argNumColors
	 * @param argPlayer1
	 * @param argPlayer2
	 */
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
		
		this.slots = new TreeMap<Integer, Slot>();
		for(int i=1; i <= this.numGuesses; i++){
			Slot slot = new Slot(this, getAndIncrementSlotSeq());
			this.slots.put(slot.getSlotNum(), slot);
		}
		
		
		this.secretSlot = new Slot(this, this.slotSeq + 1);
		this.secretSlotFinalized = false;
		
		this.guessOptions = new ArrayList<Piece>();
		for(int i=1; i <= this.numColors; i++){
			Piece piece = null;
			//note this switch must line up with setting up computer random guesses
			switch (i){
				case 1: piece = new Piece(Piece.COLOR_RED); break;
				case 2: piece = new Piece(Piece.COLOR_BLUE); break;
				case 3: piece = new Piece(Piece.COLOR_GREEN); break;
				case 4: piece = new Piece(Piece.COLOR_BLACK); break;
				case 5: piece = new Piece(Piece.COLOR_YELLOW); break;
				case 6: piece = new Piece(Piece.COLOR_ORANGE); break;
				case 7: piece = new Piece(Piece.COLOR_PURPLE); break;
			}
			
			this.guessOptions.add(piece);
		}
		
		startGame();
	}
	
	public void startGame(){
		if(PlayerType.isPlayerComputer(this.getPlayer2())){
			setupComputerSeceret(this.getPlayer2());
		}
	}
	
	public void setupComputerSeceret(Player computerPlayer){
		SortedMap<Integer, SlotSpot> secretSpots = this.secretSlot.getSpots();
		 Random randomGenerator = new Random();
		for(Integer spotNum : secretSpots.keySet()){
			SlotSpot spot = secretSpots.get(spotNum);
			int randNum = randomGenerator.nextInt(this.numColors); //gives 0-(N-1
			randNum++; //random gives 0-N, we want 1-N
			int color = 0;
			//note must line up with setting up options
			//TODO select from available options already setup
			switch (randNum) {
			case 1:
				color = Piece.COLOR_RED;
				break;
			case 2:
				color = Piece.COLOR_BLUE;
				break;
			case 3:
				color = Piece.COLOR_GREEN;
				break;
			case 4:
				color = Piece.COLOR_BLACK;
				break;
			case 5:
				color = Piece.COLOR_YELLOW;
				break;
			case 6:
				color = Piece.COLOR_ORANGE;
				break;
			case 7:
				color = Piece.COLOR_PURPLE;
				break;
			}
			Piece piece = new Piece(color);
			spot.setPiece(piece);
		}
		
		finalizeSecretSlot();
	}
	
	public Slot getSecretSlot() {
		return secretSlot;
	}

	public Slot getCurrentSlot(){
		return this.slots.get(this.currentGuessSlotNum);
	}
	
	public Slot getSlot(int slotNum){
		return this.slots.get(slotNum);
	}
	
	public void addSecretPiece(Integer slotSpotNum, Piece secretPiece){
		if(slotSpotNum == null)
			throw new IllegalArgumentException("slotSpotNum is null");
		
		if(slotSpotNum < 1 || slotSpotNum > this.numSpots)
			throw new IllegalArgumentException("slotSpotNum is not within valid range of 1-" + this.numSpots);
		
		if(secretPiece == null)
			throw new IllegalArgumentException("secretPiece is null");
		
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
			resp.setErrorMsg("Please select a color for all the spots.");
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
		
		if(guessPiece == null)
			throw new IllegalArgumentException("guessPiece is null");
		
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
		
		Slot currentSlot = this.getCurrentSlot();
		currentSlot.setGuessEntered(true);
		calculateGuessResults(currentSlot);
		
		this.currentGuessSlotNum++;
		return resp;
	}
	
	public void calculateGuessResults(Slot slot){
		
		SortedMap<Integer, SlotSpot> secreteSpots = this.secretSlot.getSpots();
		SortedMap<Integer, SlotSpot> guessSpots = slot.getSpots();
		
		if(secreteSpots.size() != guessSpots.size())
			throw new IllegalStateException("Number of guess spots does not match number of secrete spots, cannot properly calculate guess");
		
		GuessResult guessResult = new GuessResult();
		
		//loop through secrete spots, finding right color right spot and right color wrong spot results
		SortedMap<Integer, Boolean> guessesAlreadyGivenResult = new TreeMap<Integer, Boolean>();
		SortedMap<Integer, Boolean> secretsAlreadyGivenResult = new TreeMap<Integer, Boolean>();
		
		//look for blacks
		for(Integer spotNum : secreteSpots.keySet()){
			if(secretsAlreadyGivenResult.containsKey(spotNum))
				continue;//skip this secret spot as it already matched with a guess spot
			
			//for each secrete spot, compare it to all the guess spots
			SlotSpot secreteSpot = secreteSpots.get(spotNum);
			int secereteColor = secreteSpot.getPiece().getColor();
			for(Integer guessNum : guessSpots.keySet()){
				if(guessesAlreadyGivenResult.containsKey(guessNum))
					continue;//skip this guess it already has been matched a secret spot
				
				if(secretsAlreadyGivenResult.containsKey(spotNum))
					continue;//skip this secret spot as it already matched with a guess spot
				
				SlotSpot guessSpot = guessSpots.get(guessNum);
				int guessColor = guessSpot.getPiece().getColor();
				if(guessNum == spotNum){
					//check for right color right spot
					if(secereteColor == guessColor){
						GuessResultPart part = new GuessResultPart(GuessResultPart.GUESS_RESULT_RIGHT_COLOR_RIGHT_POSITION);
						guessResult.addGuesResult(part);
						guessesAlreadyGivenResult.put(guessNum, true);
						secretsAlreadyGivenResult.put(spotNum, true);
					}
				}
			}
		}
		
		//look for whites
		for(Integer spotNum : secreteSpots.keySet()){
			if(secretsAlreadyGivenResult.containsKey(spotNum))
				continue;//skip this secret spot as it already matched with a guess spot
			
			//for each secrete spot, compare it to all the guess spots
			SlotSpot secreteSpot = secreteSpots.get(spotNum);
			int secereteColor = secreteSpot.getPiece().getColor();
			for(Integer guessNum : guessSpots.keySet()){
				if(guessesAlreadyGivenResult.containsKey(guessNum))
					continue;//skip this guess it already has been matched a secret spot
				
				if(secretsAlreadyGivenResult.containsKey(spotNum))
					continue;//skip this secret spot as it already matched with a guess spot
				
				SlotSpot guessSpot = guessSpots.get(guessNum);
				int guessColor = guessSpot.getPiece().getColor();
				
				//loop for whites
				if(secereteColor == guessColor){
					GuessResultPart part = new GuessResultPart(GuessResultPart.GUESS_RESULT_RIGHT_COLOR_WRONG_POSITION);
					guessResult.addGuesResult(part);
					guessesAlreadyGivenResult.put(guessNum, true);
					secretsAlreadyGivenResult.put(spotNum, true);
				}
			}
		}
		
		slot.setGuessResult(guessResult);
		return;
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

	public List<Piece> getGuessOptions() {
		return guessOptions;
	}

	public Integer getCurrentGuessSlotNum() {
		return currentGuessSlotNum;
	}
	
	
}
