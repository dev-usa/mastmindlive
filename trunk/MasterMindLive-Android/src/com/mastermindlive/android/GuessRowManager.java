package com.mastermindlive.android;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.widget.LinearLayout;

public class GuessRowManager {
	
	private int rowNum;
	private LinearLayout guessRowView;
	private Map<Integer,SlotSpot> slotSpots;
	private View guessResults;
	
	public GuessRowManager(int argRowNum, LinearLayout argGuessRowView){
		this.rowNum = argRowNum;
		this.guessRowView = argGuessRowView;
		this.slotSpots = new HashMap<Integer, SlotSpot>();
		
	}

	public View getGuessResults() {
		return guessResults;
	}

	public void setGuessResults(View guessResults) {
		this.guessResults = guessResults;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public LinearLayout getGuessRowView() {
		return guessRowView;
	}

	public void setGuessRowView(LinearLayout guessRowView) {
		this.guessRowView = guessRowView;
	}

	public Map<Integer, SlotSpot> getSlotSpots() {
		return slotSpots;
	}

	public void setSlotSpots(Map<Integer, SlotSpot> slotSpots) {
		this.slotSpots = slotSpots;
	}
	
	public boolean allSlotsFill(){
		for(Integer slotNum : this.slotSpots.keySet()){
			SlotSpot ss = this.slotSpots.get(slotNum);
			if(ss.getColor() == 0)
				return false;
		}
		
		return true;
	}
}
