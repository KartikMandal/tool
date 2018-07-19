
package com.kartik.tools.cobranding;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionScanner {
	
	/**
	 * Table which holds all of the CORE functions that need to be replace with xslt-friendly replacements.
	 * The first parameter contains the signature of the function call.
	 * The second parameter contains the replacement text and secondary actions to take on the 
	 * string, if any.
	 */
	private Hashtable<String, FunctionInfo> searchAndReplace;
	private String replacementValue;
	private FunctionTokenized t;
	
	public FunctionScanner() {
		searchAndReplace = new FunctionScannerInfo().Table();
		replacementValue = new String();
		t = new FunctionTokenized();
	}
	
	public String scan(String body) {
		
		StringBuffer curVal = new StringBuffer(body);
		StringBuffer returnVal = new StringBuffer(body);
		
		Iterator<Entry<String, FunctionInfo>> it = searchAndReplace.entrySet().iterator();
	
		while (it.hasNext()) {
			Entry e = it.next();
			Matcher m = Pattern.compile((String)e.getKey()).matcher(curVal);
			while(m.find()) {
				// *** DEBUG ***
				System.out.println("Found CORE command @ position " + m.start() + ", " + m.end());
				System.out.println("CORE Command value: " + curVal.substring(m.start(), m.end()));
				t.setFunctionTokenized(curVal, m.start(), m.end() );
				StringBuffer replacementString = new StringBuffer(((FunctionInfo)e.getValue()).getReplacementPattern());
				for( int x = 1; x < t.getTokens().size(); x++) {
					Matcher g = Pattern.compile("(\\$" + x + ")" ).matcher(replacementString);
					String replacementToken = EscapeSpecialChar(t.getTokens().get(x));
					replacementString = new StringBuffer( g.replaceAll(replacementToken) );
				}
				// Rebuild the return value candidate and replace it with the
				// previous iteration with the new replacement string inserted
				returnVal = new StringBuffer();
				// *** DEBUG ***
				// System.out.println("Reassembling Page");
				// System.out.println("Sequence before substitution:" );
				// System.out.println(curVal.subSequence(0, t.getFunctionStart()));
				returnVal.append(curVal.subSequence(0, t.getFunctionStart()));
				// *** DEBUG ***
				// System.out.println("**************************** Substitution: ******************************" );
				// System.out.println(replacementString);
				// System.out.println("*************************************************************************" );
				returnVal.append(replacementString);
				
				// Store this value in case it is needed externally
				replacementValue = replacementString.toString();
				
				// *** DEBUG ***
				// System.out.println("Sequence after substitution:" );
				// System.out.println(curVal.subSequence(t.getFunctionEnd(), curVal.length()));
				returnVal.append(curVal.subSequence(t.getFunctionEnd(), curVal.length()));
				
				// Start with the revised string
				curVal = returnVal;
				// Reset matcher to look for another of the same value.
				m = Pattern.compile((String)e.getKey()).matcher(curVal);
			}
		}
		
		// *** DEBUG ***
		// System.out.println("Revised Page [FINAL]: \n" + returnVal.toString());
		return returnVal.toString();
	}

	private String EscapeSpecialChar(String replacementString) {
		char[] specialChars = { '$', '(', ')'};
		for( int x = 0; x < specialChars.length; x++) {
			// NOTE: Don't remove the empty string in the next statement. 
			// It is necessary to correctly form the target text.
			StringBuffer origText = new StringBuffer( "" + specialChars[x] );
			StringBuffer replText = new StringBuffer("\\" + specialChars[x] );
			replacementString = replacementString.replace( origText.subSequence(0, origText.length()), replText.subSequence(0, replText.length()) );
		}
		return replacementString;
	}

	public void setSearchAndReplace(Hashtable<String, FunctionInfo> searchAndReplace) {
		this.searchAndReplace = searchAndReplace;
	}
	
	public String getReplacementValue() {
		return replacementValue;
	}
	
	public ArrayList<String> getTokens() {
		return t.getTokens();
	}
}
	
