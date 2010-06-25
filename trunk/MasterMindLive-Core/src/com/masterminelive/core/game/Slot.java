package com.masterminelive.core.game;

import java.util.HashMap;
import java.util.Map;

public class Slot {
	private Integer slotNum;
	private Boolean guessEntered;
	private MasterMindGame game;
	private Map<Integer, SlotSpot> spots;
	
	public Slot(MasterMindGame argGame, Integer argSlotNum){
		if(slotNum == null)
			throw new IllegalArgumentException("slotNum is null");
		
		if(argGame == null)
			throw new IllegalArgumentException("argGame is null");
		
		this.game = argGame;
		this.guessEntered = false;
		this.slotNum = argSlotNum;
		
		/* Create empty slot spots */
		this.spots = new HashMap<Integer, SlotSpot>();
		for(int i=1; i <= this.game.getNumSpots(); i++){
			SlotSpot slotSpot = new SlotSpot();
			this.spots.put(i, slotSpot);
		}
	}

	public Map<Integer, SlotSpot> getSpots() {
		return spots;
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
	
	

}
