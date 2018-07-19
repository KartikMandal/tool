
package com.kartik.tools.cobranding;

public class XSLTInfo {
	
	/**
	 * Pattern that is searched for when processing original files to do util:getCobrandableStringValue()
	 * replacement within a <xsl:value-of select="..."/> statement. For example:
	 * <xsl:value-of select="util:getCobrandableStringValue(/AlertMessage/Profile/AlertRecieverProfile/cobrandId/text(),
	 * 'com.java.core.alert.base.ftA17.name')" /> would be replaced by the value of the 
	 * 'com.java.core.alert.base.ftA17' property.
	 */
	private static String xsltDataBegin = "(<\\s*xsl:value-of\\s*select\\s*=\\s*(\\\"|'))";
	private static String xsltEnd = "[^>]+(\\2\\s*/>)";
	
	private FunctionInfo signature;
	
	public enum type { data };
	public enum postScanAction { none, ReplaceWithPropertyValue };
	
	private postScanAction secondaryAction;
	private type callType;
	private StringBuffer replacementText;
	private String key;
		 
	XSLTInfo(FunctionInfo signature) {
		this.signature = signature;
		this.secondaryAction = postScanAction.none;
		this.callType = type.data;
		this.replacementText = new StringBuffer();
	}

	XSLTInfo(FunctionInfo signature, type callType, postScanAction secondaryAction) {
		this.signature = signature;
		this.secondaryAction = secondaryAction;
		this.callType = callType;
		this.replacementText = new StringBuffer();
	}

	XSLTInfo(FunctionInfo signature, type callType, postScanAction secondaryAction, String ReplacementText) {
		this.signature = signature;
		this.secondaryAction = secondaryAction;
		this.callType = callType;
		this.replacementText = new StringBuffer(ReplacementText);
	}

	public void setSecondaryAction(postScanAction secondaryAction) {
		this.secondaryAction = secondaryAction;
	}
	
	public postScanAction getSecondaryAction() {
		return secondaryAction;
	}

	public FunctionInfo getSignature() {
		return signature;
	}

	public StringBuffer getReplacementText() {
		return replacementText;
	}
	
	public String getSearchRegex() {
		StringBuffer retRegex = new StringBuffer();
		
		switch(this.callType) {
			case data: 
				retRegex.append(xsltDataBegin);
				retRegex.append(key);
				retRegex.append(xsltEnd);
		}
		return retRegex.toString();
	}

	public void setSearchRegex(type callType) {
		this.callType = callType;
	}
}
