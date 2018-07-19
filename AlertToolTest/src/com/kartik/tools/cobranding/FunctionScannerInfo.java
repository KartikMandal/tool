
package com.kartik.tools.cobranding;

import java.util.Hashtable;

public class FunctionScannerInfo {
	/**
	 * Table which holds all of the CORE functions that need to be replace with xslt-friendly replacements.
	 * The first parameter contains the signature of the function call.
	 * The second parameter contains the replacement text and secondary actions to take on the 
	 * string, if any.
	 */
	private static Hashtable<String, FunctionInfo> searchAndReplace;
	
	public FunctionScannerInfo() {
		searchAndReplace = new Hashtable<String, FunctionInfo>();
		searchAndReplace.put("util:getExtAutoPayStatus", new FunctionInfo("true"));
		searchAndReplace.put("util:getAutoPayStatusString", new FunctionInfo("AUTOPAY_SET_UP"));
		searchAndReplace.put("util:getContentServiceAutopaySupprt", new FunctionInfo("AUTOPAY_SET_UP"));
		searchAndReplace.put("util:getPaymentErrorMessage", new FunctionInfo("/AlertMessageProfile/AlertRecieverProfile/getPaymentErrorMessage/text\\(\\)"));
		searchAndReplace.put("util:getCurrencySymbol", new FunctionInfo("$2"));
		searchAndReplace.put("util:getCobrandableStringValue", new FunctionInfo("$2"));
		searchAndReplace.put("util:formatBudgetAmount", new FunctionInfo("$1"));
		searchAndReplace.put("util:getCurrencySymbol", new FunctionInfo("$currencySymbol"));
		searchAndReplace.put("util:formatAmount", new FunctionInfo("$1"));
		searchAndReplace.put("util:maskAccountNumber", new FunctionInfo("$1"));
		searchAndReplace.put("util:getDateInFormatXSDate", new FunctionInfo("$1"));
		searchAndReplace.put("util:formatAmount", new FunctionInfo("$1"));
		searchAndReplace.put("util:maskAccountNumber", new FunctionInfo("$1"));
		searchAndReplace.put("util:getDateInFormatXSDate", new FunctionInfo("$1"));
		searchAndReplace.put("util:getImage", new FunctionInfo("$1"));
		searchAndReplace.put("util:getCategoryImage", new FunctionInfo("$1"));
	}

	public Hashtable<String, FunctionInfo> Table() {
		return searchAndReplace;
	}

}
