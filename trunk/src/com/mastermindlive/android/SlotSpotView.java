package com.mastermindlive.android;

import android.widget.ImageView;

import com.masterminelive.core.game.SlotSpot;

public class SlotSpotView extends SlotSpot {

	private ImageView slotView;
	private int rowNum;
	private int slotSpotNum;
	
	public SlotSpotView(int argRowNum, int argSlotSpotNum){
		super();
		this.rowNum = argRowNum;
		this.slotSpotNum = argSlotSpotNum;
	}

	public ImageView getSlotView() {
		return slotView;
	}

	public void setSlotView(ImageView slotView) {
		this.slotView = slotView;
	}

	public int getRowNum() {
		return rowNum;
	}

	public int getSlotSpotNum() {
		return slotSpotNum;
	}
	
}
