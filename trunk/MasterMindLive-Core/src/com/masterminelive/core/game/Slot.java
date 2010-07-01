package com.masterminelive.core.game;

import java.util.SortedMap;
import java.util.TreeMap;

public class Slot {
	private Integer slotNum;
	private Boolean guessEntered;
	private MasterMindGame game;
	private SortedMap<Integer, SlotSpot> spots;
	private GuessResult guessResult;
	
	public Slot(MasterMindGame argGame, Integer argSlotNum){
		if(argSlotNum == null)
			throw new IllegalArgumentException("slotNum is null");
		
		if(argGame == null)
			throw new IllegalArgumentException("argGame is null");
		
		this.game = argGame;
		this.guessEntered = false;
		this.slotNum = argSlotNum;
		
		/* Create empty slot spots */
		this.spots = new TreeMap<Integer, SlotSpot>();
		for(int i=1; i <= this.game.getNumSpots(); i++){
			SlotSpot slotSpot = new SlotSpot();
			this.spots.put(i, slotSpot);
		}
	}

	public SortedMap<Integer, SlotSpot> getSpots() {
		return spots;
	}


	public MasterMindGame getGame() {
		return game;
	}

	public Boolean allSlotSpotsFilled(){
		for(Integer i : this.spots.keySet()){
			if(this.spots.get(i).isEmpty())
				return false;
		}
		
		return true;
	}
	
	public Integer getSlotNum() {
		return slotNum;
	}

	public Boolean getGuessEntered() {
		return guessEntered;
	}

	public void setGuessEntered(Boolean guessEntered) {
		this.guessEntered = guessEntered;
	}

	public GuessResult getGuessResult() {
		return guessResult;
	}

	public void setGuessResult(GuessResult guessResult) {
		this.guessResult = guessResult;
	}
	
	

}
