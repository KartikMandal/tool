
package com.kartik.tools.cobranding;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.util.Map.Entry;


import com.kartik.tools.cobranding.XSLTInfo.postScanAction;
import com.kartik.tools.cobranding.XSLTInfo.type;

/**
 * @author Chris Cicchetti
 *
 */
public class XSLTScanner {
	
	private FunctionScanner fs;
	private Properties prop;
	private String text;
	private boolean doFuncScanPost;
	
	/**
	 * Table which holds all of the CORE functions that need to be replace with xslt-friendly replacements.
	 * The first parameter contains the signature of the function call.
	 * The second parameter contains the replacement text and secondary actions to take on the 
	 * string, if any.
	 */
	private Hashtable<String, FunctionInfo> searchAndReplaceFunc;
	
	/**
	 * List which holds all of the xslt calls that need to be updated or replaced.
	 */
	private ArrayList<XSLTInfo> searchAndReplaceXSLT;
	
	public XSLTScanner(Properties alertProperties, String body, boolean doFuncScanPost) {
		fs = new FunctionScanner();
		prop = alertProperties;
		text = new String( body );
		searchAndReplaceFunc = new FunctionScannerInfo().Table();
		searchAndReplaceXSLT = new ArrayList<XSLTInfo>();
		this.doFuncScanPost = doFuncScanPost;
		
		// **************************************************************
		// Define any XSLT / Function pairs that need to be replaced HERE
		// **************************************************************
		searchAndReplaceXSLT.add(new XSLTInfo(searchAndReplaceFunc.get("util:getCobrandableStringValue"),
				type.data,
				postScanAction.ReplaceWithPropertyValue));
	}

	public String scan() {
		
		Iterator<XSLTInfo> it = searchAndReplaceXSLT.iterator();
	
		while (it.hasNext()) {
			XSLTInfo thisInfo = (XSLTInfo)it.next();
			String searchRegex = thisInfo.getSearchRegex();
			Matcher m = Pattern.compile(searchRegex).matcher(text);
			while(m.find()) {
				int commandStart = m.start(), commandEnd = m.end();
				String command = text.substring(commandStart, commandEnd);
				// *** DEBUG ***
				System.out.println();
				System.out.println("Found XSLT command @ position " + commandStart + ", " + commandEnd);
				System.out.println("XSLT command value: " + command );
				String Value = fs.scan(command);
				
				// Assemble the pre-command buffer
				StringBuffer buff = new StringBuffer();
				buff.append(text.substring(0, commandStart));
				
				switch(thisInfo.getSecondaryAction()) {
				case none:
					buff.append(Value);
					break;
				case ReplaceWithPropertyValue:	
					buff.append(ReplaceWithPropertyValue(fs.getReplacementValue(), prop));
					break;
				default:
					System.out.println("[ERROR] - unknown XSLT post scan action value.");
					break;
				}
				
				// Append the post command buffer
				buff.append(text.substring(commandEnd, text.length()));
				
				// Recast the rebuild buffer
				text = buff.toString();
				// Reset matcher to look for another of the same value.
				m = Pattern.compile(thisInfo.getSearchRegex()).matcher(text);
			}
		}
		
		// Perform optional Function scan
		if(doFuncScanPost) text = fs.scan(text);
		
		return text;
	}

	private String ReplaceWithPropertyValue(String ValueToReplace, Properties prop) {
		System.out.print("Replaced value: " + ValueToReplace);
		// Remove any single or double quotes before passing to find property
		ValueToReplace = ValueToReplace.replace("\'", "");
		ValueToReplace = ValueToReplace.replace("\"", "");
		// Return property value for value passed in
		String propertyValue = prop.getProperty(ValueToReplace, "[FAIL: "+ ValueToReplace +"]");
		// *** Debug
		System.out.println(" with: " + propertyValue);
		return propertyValue;
	}
}
