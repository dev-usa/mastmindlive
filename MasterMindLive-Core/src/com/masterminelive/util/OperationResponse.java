package com.masterminelive.util;

public class OperationResponse {

	private String warningMsg;
	private String errorMsg;
	
	public Boolean hadWarnings(){
		if(this.warningMsg == null)
			return false;
		else
			return true;
	}
	
	public Boolean hadErrors(){
		if(this.errorMsg == null)
			return false;
		else
			return true;
	}
	
	public String getWarningMsg() {
		return warningMsg;
	}
	public void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
}
