
package com.kartik.tools.cobranding;

public class FunctionInfo {
	
	private StringBuilder replacementPattern;
	
	private enum postScanAction { none, insertRandomNumber };
	
	private postScanAction secondaryAction;
	 
	FunctionInfo(String replacementPattern) {
		this.replacementPattern = new StringBuilder(replacementPattern);
	}

	FunctionInfo(String replacementPattern, postScanAction secondaryAction ) {
		this.replacementPattern = new StringBuilder(replacementPattern);
		this.secondaryAction = secondaryAction;
	}
	
	public postScanAction getSecondaryAction() {
		return secondaryAction;
	}

	public StringBuilder getReplacementPattern() {
		return replacementPattern;
	}
}
