package com.masterminelive.core.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.masterminelive.core.players.Player;
import com.masterminelive.core.players.PlayerType;
import com.masterminelive.util.OperationResponse;

public class MasterMindGame{

	//different states the game can be in
	public static int GAME_STATE_STARTED = 1;
	public static int GAME_STATE_SECRET_CODE_ENTERED = 2;
	public static int GAME_STATE_CODE_BREAKER_GUESSING = 3;
	public static int GAME_STATE_CODE_BREAKER_BROKE_CODE = 4;
	public static int GAME_STATE_CODE_BREAKER_RAN_OUT_OF_GUESSES = 5;
	
	public static int MAX_ALLOWED_GUESSES = 12;
	public static int MAX_ALLOWED_SPOTS = 6;
	public static int MAX_ALLOWED_COLORS = 7;
	
	private int slotSeq; //internal numbering sequence to give slots a unique id
	private int numGuesses; //number of chances the codeBreaker has to try and break the code
	private int numSpots; //number of slotspots for the code
	private int numColors; //number of possible colors the code can contain
	private Player codeBreaker; //guessing player
	private Player codeCreator; //secret code creating player
	private SortedMap<Integer, Slot> slots; //the guess rows/slots for the board
	private List<Piece> guessOptions; //the color pin/piece options for a given guess slot spot
	private int currentGuessSlotNum; //current guess slot number
	private Slot secretSlot; //secret code slot
	private int gameState; //current state of the game
	
	/**
	 * Constructor for a MasterMindLive Game
	 * Validates input
	 * assigns member variables
	 * creates necessary empty slots
	 * sets up color piece options the code creator and code breaker can use
	 * starts the game
	 * 
	 * @param argNumSpots - number of spots for the code pieces, the greater the number the harder the code is to break
	 * @param argNumGuesses - number of attempts the code breaker has to break the code
	 * @param argNumColors - number of colors/piece types available for the code
	 * @param argCodeBreaker - code breaker player
	 * @param argCodeCreator - code creator player
	 */
	public MasterMindGame(Integer argNumSpots, Integer argNumGuesses, Integer argNumColors, Player argCodeBreaker, Player argCodeCreator){
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
		
		if(argCodeBreaker == null)
			throw new IllegalArgumentException("argCodeBreaker player is null");
		
		if(argCodeCreator == null)
			throw new IllegalArgumentException("argCodeCreator player is null");
		
		this.numSpots = argNumSpots;
		this.numGuesses = argNumGuesses;
		this.numColors = argNumColors;
		this.codeBreaker = argCodeBreaker;
		this.codeCreator = argCodeCreator;
		this.slotSeq = 1;
		this.currentGuessSlotNum = 1;
		
		this.slots = new TreeMap<Integer, Slot>();
		for(int i=1; i <= this.numGuesses; i++){
			Slot slot = new Slot(this, getAndIncrementSlotSeq());
			this.slots.put(slot.getSlotNum(), slot);
		}
		
		
		this.secretSlot = new Slot(this, this.slotSeq + 1);
		
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
		this.gameState = GAME_STATE_STARTED;
		
		//if computer player for code creator, create the code
		if(PlayerType.isPlayerComputer(this.codeCreator)){
			setupComputerSeceret(this.codeCreator);
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
		
		this.gameState = GAME_STATE_SECRET_CODE_ENTERED;	
		return resp;
	}
	
	public void startCodeBreaking(){
		this.gameState = GAME_STATE_CODE_BREAKER_GUESSING;
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
	
	
	public GuessResponse guess(){
		GuessResponse resp = new GuessResponse();
		Slot slot = getCurrentSlot();
		if(slot.allSlotSpotsFilled() == false){
			resp.setErrorMsg("Please guess a color for all the spots");
			return resp;
		}
		
		Slot currentSlot = this.getCurrentSlot();
		currentSlot.setGuessEntered(true);
		calculateGuessResults(currentSlot);
		
		//sent boolean if winning guess
		boolean winningGuess = currentSlot.getGuessResult().getWinningGuess();
		resp.setWinningGuess(winningGuess); 
		if(winningGuess){
			this.gameState = GAME_STATE_CODE_BREAKER_BROKE_CODE;
		}else{
			if(this.currentGuessSlotNum == this.numGuesses){
				//player ran out of guesses to crack code, end game
				this.gameState = GAME_STATE_CODE_BREAKER_RAN_OUT_OF_GUESSES;
			}else{
				//increment the current guess slot
				this.currentGuessSlotNum++;
			}
		}
		return resp;
	}
	
	public void calculateGuessResults(Slot slot){
		
		SortedMap<Integer, SlotSpot> secreteSpots = this.secretSlot.getSpots();
		SortedMap<Integer, SlotSpot> guessSpots = slot.getSpots();
		
		if(secreteSpots.size() != guessSpots.size())
			throw new IllegalStateException("Number of guess spots does not match number of secrete spots, cannot properly calculate guess");
		
		GuessResult guessResult = new GuessResult(slot);
		
		//loop through secrete spots, finding right color right spot (black) and right color wrong spot(white) results
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

	public List<Piece> getGuessOptions() {
		return guessOptions;
	}

	public Integer getCurrentGuessSlotNum() {
		return currentGuessSlotNum;
	}

	public Player getCodeBreaker() {
		return codeBreaker;
	}

	public Player getCodeCreator() {
		return codeCreator;
	}

	public int getGameState() {
		return gameState;
	}
	
	
}
