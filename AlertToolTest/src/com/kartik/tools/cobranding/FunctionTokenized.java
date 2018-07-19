
package com.kartik.tools.cobranding;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionTokenized {
	private int functionStart;
	private int functionEnd;
	private ArrayList<String> tokens;
	
	private Pattern pAtFirstParens = Pattern.compile("\\s*\\(");
	private Pattern pAtLastParens = Pattern.compile(".*\\)$");
	private Pattern pAtOpenOrCloseParens = Pattern.compile("[^\\(\\)]*(\\)|\\()");
	private Pattern pAtComma = Pattern.compile("([^,]*),");
	
	/**
	 * 	FunctionTokenized breaks a function call into its constituent tokens.
	 *  The zeroth element returned is the function signature. Each element afterwards is a 
	 *  function argument. For instance:
	 *  util:getCobrandableStringValue(/AlertMessage/Profile/AlertRecieverProfile/cobrandId/text(), 'com.Java.core.alert.base.ftA17.name') 
	 *  would get broken into three tokens:
	 *  	0: util:getCobrandableStringValue
	 *  	1: /AlertMessage/Profile/AlertRecieverProfile/cobrandId/text()
	 * 		2: 'com.Java.core.alert.base.ftA17'
	 **/ 
	public FunctionTokenized() {
		tokens = new ArrayList<String>();
		functionStart = 0;
		functionEnd = 0;
	}
	
	public FunctionTokenized(StringBuffer body, int matchStart, int matchEnd) {
		tokens = new ArrayList<String>();
		setFunctionTokenized(body, matchStart, matchEnd);
	}
	
	public void setFunctionTokenized(StringBuffer body, int matchStart, int matchEnd) {
	
		// Reset the token list
		tokens.clear();
		// The int parameters passed in define the beginning and ending of a found function call.
		// Extract that to the first (zeroth) token position
		tokens.add(body.substring(matchStart, matchEnd));
		
		// Store the position of the start of the function
		functionStart = matchStart;
		
		functionEnd = GetParamEndPos(body, matchStart, body.length());
		
		GetFunctionTokens(body, matchEnd, functionEnd);
	}
	
	private void GetFunctionTokens(StringBuffer body, int matchStart, int matchEnd) {
		
		// Create a matcher to (initially) find the open parenthesis
		Matcher m = pAtFirstParens.matcher(body);
		m.region(matchStart, matchEnd);
		
		// *** DEBUG ***
		System.out.println("Function body to break into tokens: " + body.substring(matchStart, matchEnd) );
		
		// Move the match region past the first parenthesis
		if( !m.usePattern(pAtFirstParens).find() ) FailFunctionTokenized(m, "Could not find open parenthesis");
		matchStart = m.end();
		
		// Make the params w/out start and end parens as the new search region
		m.region(matchStart, matchEnd);

		// Remove the close parenthesis from the match range
		if( !m.usePattern(pAtLastParens).find() ) FailFunctionTokenized(m, "Could not find close parenthesis");
		matchEnd = m.end() - 1;
		
		// Set the new range
		m.region(matchStart, matchEnd);
		
		// *** DEBUG ***
		System.out.println("Function body to break into tokens minus open and close parethesis: " + body.substring(matchStart, matchEnd) );
		
		// Split the parameters at comma boundaries
		m = m.usePattern(pAtComma);
		int startOfRestOfString = matchStart;
		while(m.find()) {
			// *** DEBUG ***
			System.out.println("Token Found: " + m.group(1).toString() );			
			tokens.add(m.group(1).toString());
			startOfRestOfString = m.end();
		}
		
		// *** DEBUG ***
		System.out.println("Token Found: " + body.subSequence(startOfRestOfString, m.regionEnd()) );	

		// Place the remainder of the string into 
		tokens.add(body.substring(startOfRestOfString, m.regionEnd()));
	}

	private int GetParamEndPos(StringBuffer body, int regionStart, int regionEnd) {
		
		// Create a matcher to (initially) find the open parenthesis
		Matcher m = pAtFirstParens.matcher(body);
		m.region(regionStart, regionEnd);
		
		// Consume any whitespace before the first parenthesis
		if( !m.find() ) FailFunctionTokenized(m, "Could not find open parenthesis");
		
		// We now have an open parenthesis. Start count at one.
		int parensCount = 1;
		
		// Setup to count open and close parenthesis AFTER the first open.
		// Note that found parenthesis is stored in group(1) after find.
		m = m.usePattern(pAtOpenOrCloseParens);
		
		// We are now at the position past the first parenthesis.
		// Count parentheses to find the position of the last close parenthesis.
		while( parensCount> 0 && m.find()) {
			if( m.group(1).equals("("))
				parensCount += 1;
			else if(m.group(1).equals(")"))
				parensCount -= 1;
		}
		
		return m.end();
	}

	public int getFunctionStart() {
		return functionStart;
	}

	public int getFunctionEnd() {
		return functionEnd;
	}

	public int getFunctionLength() {
		return functionEnd - functionStart;
	}

	public ArrayList<String> getTokens() {
		return tokens;
	}
	
	private void FailFunctionTokenized(Matcher m, String ErrMsg) {
		//Nothing matched at this point, so it must be an error. Grab a dozen or so characters
		//at our current location so that we can issue an informative error message
		m.usePattern(Pattern.compile("\\G(?s).{1,12}")).find();
		System.out.println("[FAIL]" + ErrMsg + " at ’" + m.group() + "’");
		System.exit(1);
	}
}