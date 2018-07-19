
package com.kartik.tools.cobranding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * This class is used to execute alert testing for a particular cobrand.
 * It will load all pfiles from an cobranded directory specified by
 * configuration, execute all the XSLTs and then create an output version
 * of everything.
 * 
 * @author jordan w/ updates by cicchetti
 */
public class AlertTestingTool {
	
	/**
	 * Pattern that is searched for when processing original files to do util:getCobrandableStringValue()
	 * replacement within a <xsl:value-of select="..."/> statement. For example:
	 * <xsl:value-of select="util:getCobrandableStringValue(/AlertMessage/Profile/AlertRecieverProfile/cobrandId/text(),
	 * 'com.kartik.core.alert.base.ftA17.name')" /> would be replaced by the value of the 
	 * 'com.kartik.core.alert.base.ftA17' property.
	 */
	public static final Pattern CBSVPattern = Pattern.compile("util:getCobrandableStringValue[^'\">]+(\"|')([^'\">]+)\\1\\s*\\)");
		
	/**
	 * Pattern that is searched for when processing original files to do variable
	 * replacement.  For example a {cobrandId} would be replaced by that
	 * property.
	 */
	public static final Pattern replacePattern = Pattern.compile("(.*)\\{(.*?)\\}(.*)");
	
	/**
	 * Pattern in the configuration file that is used to define test cases.  Values are
	 * expected to be true so this would enable the ftA43 test:<br/>
	 * testcase.ftA43=true
	 */
	public static final Pattern testCasePattern = Pattern.compile("testcase\\.([^\\.]+)");
		
	/**
	 * Hold the configuration properties that are loaded from the file system.
	 * By default this is class loaded as:<br/>
	 * <code>
	 * /com/kartik/tools/cobranding/AlertTransformTestConfiguration.properties<br/>
	 * </code>
	 * But this can be overridden with the following java property:<br/>
	 * <code>
	 * -DtestConfigurationLocation=/com/kartik/tools/cobranding/AlertTransformTestConfiguration.properties
	 * </code>
	 */
	protected Properties configurationProperties;
	
	/**
	 * After being built out this holds the location of the CobrandableStrings.properties
	 * file that will be used.  e.g.<br/>
	 * /Users/jordan/javadev/cobrands/razor9/metavante/10005352/alerts/classes/com/kartik/cobrands/10005352/prop/core/CobrandableStrings.properties
	 */
	protected String alertCobStringFile;

	/**
	 * After being built out this holds the location of the CobrandableParams.properties
	 * file that will be used.  e.g.<br/>
	 * /Users/jordan/javadev/cobrands/razor9/metavante/10005352/alerts/classes/com/kartik/cobrands/10005352/prop/core/CobrandableParams.properties
	 */
	protected String alertCobParamFile;
	
	/**
	 * After being built up this will contain the directory where all the pfile for
	 * the cobrand are stored.  e.g.<br/>
	 * /Users/jordan/javadev/cobrands/razor9/metavante/10005352/alerts/classes/com/kartik/cobrands/10005352/pfile/core/alert/base
	 */
	protected String baseCobrandPfilePath;
	
	
	/**
	 * After being built up this will contain the directory where all the pfile for
	 * the core version are stored.  e.g.<br/>
	 * /Users/jordan/javadev/razor/core/9.0.3/properties/com/kartik/pfile/core/alert/base
	 */
	protected String baseCorePfilePath;
	
	/**
	 * After being built up this will contain the location of the base CobrandableStrings file.
	 * e.g. <br/>
	 * /Users/jordan/javadev/razor/core/9.0.3/properties/com/kartik/prop/core/CobrandableStrings.properties
	 */
	protected String baseCobStringFile;

	/**
	 * After being built up this will contain the location of the base CobrandableParams file.
	 * e.g. <br/>
	 * /Users/jordan/javadev/razor/core/9.0.3/properties/com/kartik/prop/core/CobrandableParams.properties
	 */
	protected String baseCobParamFile;
	
	/**
	 * After being built up this will contain the location of the cobranding alert header file.
	 * e.g. <br/>
	 * /Users/jordan/javadev/cobrands/razor9/metavante/10005352/alerts/classes/com/kartik/cobrands/10005352/pfile/core/alert/base/alert_greeting_html.pfile
	 */
	protected String alertHeaderFile;
	
	/**
	 * After being built up this will contain the location of the cobranding alert footer file.
	 * e.g. <br/>
	 * /Users/jordan/javadev/cobrands/razor9/metavante/10005352/alerts/classes/com/kartik/cobrands/10005352/pfile/core/alert/base/alert_footer_html.pfile
	 */
	protected String alertFooterFile;
	
	/**
	 * This contains the classpath location of the testcaseData.  it will default
	 * to <code>/com/kartik/tools/cobranding/testdata</code>
	 */
	protected String testcaseDataLocation;
	
	/**
	 * Holds the location of the output directory.
	 */
	protected File outputDirectory;
		
	/**
	 * Constructor generates the main class and then loads all of the configurations
	 * from the property file and builds up the full paths.
	 * 
	 * @throws IOException indicates and issue loading from the configuration files
	 */
	public AlertTestingTool() throws IOException {
		configurationProperties = new Properties();
		
		String configurationFileLocation =
			System.getProperty("testConfigurationLocation", "/com/kartik/tools/cobranding/config/AlertTransformPfmTestConfiguration.properties");
		
		System.out.println("Loading configuration from: " + configurationFileLocation);

		InputStream configurationStream = 
			this.getClass().getResourceAsStream(configurationFileLocation);
				
		configurationProperties.load(configurationStream);
		
		System.out.println("Keys in configuration file: " + configurationProperties.size());
		
		// Determine the location of the output directory
		String outputDirectoryString = configurationProperties.getProperty("outputDirectory");
		outputDirectory = new File(outputDirectoryString + File.separator + System.currentTimeMillis());
		boolean mkdirSuccess = outputDirectory.mkdirs();
		if(!mkdirSuccess) {
			throw new IOException("Failed to create directory: " + outputDirectory.getName());
		}
		
		// Location of all the test case data.
		testcaseDataLocation = configurationProperties.getProperty("testcaseDataLocation", "/com/kartik/tools/cobranding/testdata");
		
		// Check if there is a smart path.  the smart path is used to build all of the
		// cobranding paths using the cobrand id as a standard model of the cobrand directory
		// structure.  This is typically set to "false" if you are trying to run against
		// the base application paths.
		boolean smartPath = Boolean.parseBoolean(configurationProperties.getProperty("smartPath"));
		
		String baseApplicationFileLocation = configurationProperties.getProperty("baseApplicationFileLocation");
        baseApplicationFileLocation = System.getProperty("baseApplicationFileLocation", baseApplicationFileLocation);
        
        testDirectoryExistence(
            "baseApplicationFileLocation", 
            baseApplicationFileLocation,
            configurationFileLocation,
            "this is the location where //razor/core is sync'ed to from perforce.");
        
		String baseApplicationVersion = configurationProperties.getProperty("baseApplicationVersion");
		
		String cobrandBaseFilePath = configurationProperties.getProperty("cobrandBaseFilePath");
        cobrandBaseFilePath = System.getProperty("cobrandBaseFilePath", cobrandBaseFilePath);
        testDirectoryExistence(
            "cobrandBaseFilePath", 
            cobrandBaseFilePath,
            configurationFileLocation,
            "this is the location where alert pfile are sync'ed to from perforce.  "
            + "For base application testing this will be the location of ["
            + "//razor/core/9.0.3/properties/com/kartik/pfile/core/alert/base"
            + "] and for cobrand-level testing this will be the location of ["
            + "//cobrands/razor9/metavante"
            + "]");
        
		String cobrandId = configurationProperties.getProperty("cobrandId");
		
		String baseApplicationFilePath =
			baseApplicationFileLocation 
			+ File.separator 
			+ baseApplicationVersion 
			+ File.separator
			+ "properties"
			+ File.separator
			+ "com"
			+ File.separator
			+ "kartik";
		
		baseCorePfilePath =
			baseApplicationFilePath
			+ File.separator
			+ "pfile"
			+ File.separator
			+ "core"
			+ File.separator
			+ "alert"
			+ File.separator
			+ "base";
		
		baseCobParamFile =
			baseApplicationFilePath
			+ File.separator
			+ "prop"
			+ File.separator
			+ "core"
			+ File.separator
			+ "CobrandableParams.properties";

		baseCobStringFile =
			baseApplicationFilePath
			+ File.separator
			+ "prop"
			+ File.separator
			+ "core"
			+ File.separator
			+ "CobrandableStrings.properties";

		String baseCobrandPropertyPath =
			cobrandBaseFilePath 
			+ File.separator 
			+ cobrandId 
			+ File.separator
			+ "alerts"
			+ File.separator
			+ "classes"
			+ File.separator
			+ "com"
			+ File.separator
			+ "kartik"
			+ File.separator
			+ "cobrands" 
			+ File.separator
			+ cobrandId
			+ File.separator
			+ "prop"
			+ File.separator
			+ "core";
		
		alertCobStringFile = 
			baseCobrandPropertyPath 
			+ File.separator 
			+ "CobrandableStrings.properties";
		
		alertCobParamFile = 
			baseCobrandPropertyPath 
			+ File.separator 
			+ "CobrandableParams.properties";
		
		if(smartPath) {
			baseCobrandPfilePath =
				cobrandBaseFilePath 
				+ File.separator 
				+ cobrandId 
				+ File.separator
				+ "alerts"
				+ File.separator
				+ "classes"
				+ File.separator
				+ "com"
				+ File.separator
				+ "kartik"
				+ File.separator
				+ "cobrands" 
				+ File.separator
				+ cobrandId
				+ File.separator
				+ "pfile"
				+ File.separator
				+ "core"
				+ File.separator
				+ "alert"
				+ File.separator
				+ "base";
		} else {
			baseCobrandPfilePath =
				cobrandBaseFilePath;
		}
			
		alertHeaderFile =
			baseCobrandPfilePath
			+ File.separator
			+ "alert_greeting_html.pfile";
		
		alertFooterFile =
			baseCobrandPfilePath
			+ File.separator
			+ "alert_footer_html.pfile";
			
			
		// Output a bunch of properties
		System.out.println("baseApplicationFileLocation=" + baseApplicationFileLocation);
		System.out.println("baseApplicationVersion=" + baseApplicationVersion);
		System.out.println("baseCobrandPfilePath=" + baseCobrandPfilePath);
		System.out.println("cobrandBaseFilePath=" + cobrandBaseFilePath);
		System.out.println("cobrandId=" + cobrandId);
		System.out.println("alertCobStringFile=" + alertCobStringFile);
		System.out.println("alertCobParamFile=" + alertCobParamFile);
		System.out.println("baseCobStringFile=" + baseCobStringFile);
		System.out.println("baseCobParamFile=" + baseCobParamFile);
		System.out.println("alertHeaderFile=" + alertHeaderFile);
		System.out.println("alertFooterFile=" + alertFooterFile);
		

	}
	
	/**
	 * Executes all of the enabled tests based on the configuration against
	 * the XLSTs.  
	 * 
	 * @throws IOException Indicates an error loading the base property files.
	 */
	public void test() throws IOException {
		// Load all of the properties files into a single property file
		// that will be used for property-based replacement.
		// The order should be the base files and then load the application
		// level files.		
		Properties alertProperties = loadCobrandingProperties();

		
		// Load the header files from a pfile
		String header;
		try {
			header = generateFromPFile(alertProperties, new FileInputStream(alertHeaderFile));
		} catch (IOException e) {
			header = e.toString();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			header = e.toString() + "\n" + sw.toString();
		}
		
		// Load the footer files from a pfile
		String footer;
		try {
			footer = generateFromPFile(alertProperties, new FileInputStream(alertFooterFile));
		} catch (IOException e) {
			footer = e.toString();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			footer = e.toString() + "\n" + sw.toString();
		}

		// Start creating the index html page.  This is the page
		// that will provide links to all the generated alerts
		// html files.
		StringBuffer indexHtml = new StringBuffer();
		indexHtml.append("<html><head><title>Test Results</title></head><body><h1>Test Results</h1><ol>");
		
		// Loop through all of the test cases that are enabled inside of the
		// properties file and execute the test.
		// This will produce a separate HTML for each test case run as well
		// as add a line to the index html file.
		Enumeration<String> propertyNames = (Enumeration<String>) configurationProperties.propertyNames();
		while(propertyNames.hasMoreElements()) {
			String propertyName = propertyNames.nextElement();
			Matcher testCaseMatcher = testCasePattern.matcher(propertyName);
			if(testCaseMatcher.matches()) {
				String ruleName = testCaseMatcher.group(1);
				String result;
				
				try {
					result = executeTest(alertProperties, ruleName);
					indexHtml.append("<li>");
				} catch (Throwable e) {
					result = e.toString();
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					result = "<strong>"
						+ e.toString() 
						+ "</strong>"
						+ "<br/><pre>" 
						+ sw.toString()
						+ "</pre><br/>";
					indexHtml.append("<li><strong>ERROR: </strong>");
				}
				
				// After the execution of the test, buffer everything
				// together and then write out to file.
				StringBuffer outputBuffer = new StringBuffer();
				outputBuffer.append(header);
				outputBuffer.append(result);
				outputBuffer.append(footer);
				
				writeFile(outputDirectory, ruleName + ".html", outputBuffer.toString());
				//Sending mail open this link
				SendInlineImagesInEmails ddd=new SendInlineImagesInEmails();
				ddd.sendMail(outputBuffer.toString());
				// Add a line to the index file buffer for this test case.
				indexHtml.append("<a href=\"");
				indexHtml.append(ruleName);
				indexHtml.append(".html\">");
				indexHtml.append(ruleName);
				indexHtml.append("</a></li>");
			}
		}
		
		// Close off the html
		indexHtml.append("</ol></body></html>");
	
		// Write out the final html
		writeFile(outputDirectory, "index.html", indexHtml.toString());
		
		System.out.println("Process complete.");
		System.out.println("\n\nOverpage Written to: " 
			+ outputDirectory.getName() 
			+ File.separator 
 			+ "index.html");
	}
	
	
	/**
	 * @author kmandal 
	 * @see Executes all of the enabled tests based on the configuration against
	 * the XLSTs.  
	 * 
	 * @throws IOException Indicates an error loading the base property files.
	 */
	public void testText() throws IOException {
		// Load all of the properties files into a single property file
		// that will be used for property-based replacement.
		// The order should be the base files and then load the application
		// level files.		
		Properties alertProperties = loadCobrandingProperties();

		Enumeration<String> propertyNames = (Enumeration<String>) configurationProperties.propertyNames();
		while(propertyNames.hasMoreElements()) {
			String propertyName = propertyNames.nextElement();
			Matcher testCaseMatcher = testCasePattern.matcher(propertyName);
			if(testCaseMatcher.matches()) {
				String ruleName = testCaseMatcher.group(1);
				String result;
				try {
					result = executeTestText(alertProperties, ruleName,"text");
					
				} catch (Throwable e) {
					result = e.toString();
					
				}
				
				StringBuffer outputBuffer = new StringBuffer();
				outputBuffer.append(result);
				writeFile(outputDirectory, ruleName + ".text", outputBuffer.toString());
				
				String smsResult;
				try {
					smsResult = executeTestText(alertProperties, ruleName,"sms");
					
				} catch (Throwable e) {
					smsResult = e.toString();
					
				}
				
				StringBuffer smsOutputBuffer = new StringBuffer();
				smsOutputBuffer.append(smsResult);
				writeFile(outputDirectory, ruleName + ".sms", smsOutputBuffer.toString());
				
			}
		}
	}
	
	
	/**
	 * Executes a test for particular rule.  This will load the rules XSLT
	 * and the test data for the rule and then execute the test.
	 * 
	 * @param alertProperties the properties file that contains all the loaded properties
	 * @param ruleName the name of the rule that is being tested
	 * @return the result of test which will be the message created or a stack trace.
	 * 
	 * @throws IOException indicates an error reading one of the files needed for the test
	 * @throws TransformerException indicates an error compiling or executing the XSLT
	 */
	protected String executeTestText(Properties alertProperties, String ruleName,String type) throws IOException, TransformerException {
		String alertXsltFile = baseCobrandPfilePath
			+ File.separator
			+ ruleName
		    + "_"+type+".pfile";
				
        File testFile = new File(alertXsltFile);
        if(!testFile.exists()) {
            alertXsltFile = baseCorePfilePath
			+ File.separator
			+ ruleName
		    + "_"+type+".pfile";
        }
		
		String alertTestFilePath =
			testcaseDataLocation + "/" + ruleName + ".xml";
		
		System.out.println("Testing ["
			+ ruleName
			+ "] by pumping ["
			+ alertTestFilePath
			+ "] through ["
			+ alertXsltFile
			+ "]");
		
		InputStream alertTestFileInput =
			this.getClass().getResourceAsStream(alertTestFilePath);
		
		if(alertTestFileInput == null) {
			System.err.println("Failed to file: " + alertTestFilePath);
			return null;
		} else {
			String result = generateFromXslt(alertProperties, new FileInputStream(alertXsltFile), alertTestFileInput);
			return result;
		}
	}
	/**
	 * Helper method that loads all of the properties files including:
	 * <ol>
	 *   <li>Base CobrandableParams.properties</li>
	 *   <li>Base CobrandableStrings.properties</li>
	 *   <li>Cobranded CobrandableParams.properties</li>
	 *   <li>Cobranded CobrandableStrings.properties</li>
	 * </ol>
	 * @return
	 */
	protected Properties loadCobrandingProperties() {
		Properties alertProperties = new Properties();
		try {
			loadProperties(alertProperties, baseCobParamFile);
			//D://Perforce//kartik_manadal//razor//core\platform\properties\com\kartik\prop\core\CobrandableParams.properties 
		} catch (IOException e) {
			System.err.println("Could not find: " + baseCobParamFile);
		}

		try {
			loadProperties(alertProperties, baseCobStringFile);
			//D://Perforce//kartik_manadal//razor//core\platform\properties\com\kartik\prop\core\CobrandableStrings.properties 
		} catch (IOException e) {
			System.err.println("Could not find: " + baseCobStringFile);
		}
		
		try {
			loadProperties(alertProperties, alertCobParamFile);
	//   D://Perforce//kartik_manadal//cobrands//razor//Java\10000004\alerts\classes\com\kartik\cobrands\10000004\prop\core\CobrandableParams.properties
		} catch (IOException e) {
			System.err.println("Could not find: " + alertCobParamFile);
		}

		try {
			loadProperties(alertProperties, alertCobStringFile);
	//  D://Perforce//kartik_manadal//cobrands//razor//Java\10000004\alerts\classes\com\kartik\cobrands\10000004\prop\core\CobrandableStrings.properties
		} catch (IOException e) {
			System.err.println("Could not find: " + alertCobStringFile);
		}
		
		return alertProperties;
	}
	
	/**
	 * Loads a single property file from the file system.
	 * 
	 * @param alertProperties the property object to load the values into
	 * @param propertiesFile the path of the property file to load
	 * 
	 * @throws IOException indicates an error reading the external file
	 */
	protected void loadProperties(Properties alertProperties, String propertiesFile) throws IOException {
		Properties tempProperties =  new Properties();
		InputStream alertPropertyInputStream = new FileInputStream(propertiesFile);
		tempProperties.load(alertPropertyInputStream);
		
		alertProperties.putAll(tempProperties);
	}	
	
	/**
	 * Writes out a single file.
	 * 
	 * @param outputDirectory the directory to write the file into
	 * @param filename the name of the file to be created and written
	 * @param outputData the data to be output
	 * 
	 * @throws IOException indicates a failure in writing the file
	 */
	protected void writeFile(File outputDirectory, String filename, String outputData) throws IOException {
		File outputFile = new File(outputDirectory, filename);
		FileWriter fileWriter = new FileWriter(outputFile);
		
		fileWriter.write(outputData);
		
		fileWriter.close();
	}
	
	/**
	 * Executes a test for particular rule.  This will load the rules XSLT
	 * and the test data for the rule and then execute the test.
	 * 
	 * @param alertProperties the properties file that contains all the loaded properties
	 * @param ruleName the name of the rule that is being tested
	 * @return the result of test which will be the message created or a stack trace.
	 * 
	 * @throws IOException indicates an error reading one of the files needed for the test
	 * @throws TransformerException indicates an error compiling or executing the XSLT
	 */
	protected String executeTest(Properties alertProperties, String ruleName) throws IOException, TransformerException {
		String alertXsltFile = baseCobrandPfilePath
			+ File.separator
			+ ruleName
		    + "_html.pfile";
				
        File testFile = new File(alertXsltFile);
        if(!testFile.exists()) {
            alertXsltFile = baseCorePfilePath
			+ File.separator
			+ ruleName
		    + "_html.pfile";
        }
		
		String alertTestFilePath =
			testcaseDataLocation + "/" + ruleName + ".xml";
		
		System.out.println("Testing ["
			+ ruleName
			+ "] by pumping ["
			+ alertTestFilePath
			+ "] through ["
			+ alertXsltFile
			+ "]");
		
		InputStream alertTestFileInput =
			this.getClass().getResourceAsStream(alertTestFilePath);
		
		if(alertTestFileInput == null) {
			System.err.println("Failed to file: " + alertTestFilePath);
			return null;
		} else {
			String result = generateFromXslt(alertProperties, new FileInputStream(alertXsltFile), alertTestFileInput);
			return result;
		}
	}
		
	/**
	 * Generates a message from the XSLT
	 * 
	 * @param alertProperties the properties file that contains all the loaded properties
	 * @param alertXslt the XSLT input stream for the template to be executed
	 * @param alertTestFileInput the input stream for the test data to execute
	 * @return the message generated
	 * 
	 * @throws IOException indicates a problem reading the template or test data
	 * @throws TransformerException indicates an error compiling or executing the XSLT
	 */
	protected String generateFromXslt(Properties alertProperties, InputStream alertXslt, InputStream alertTestFileInput) throws IOException, TransformerException {
		String body = generateFromPFile(alertProperties, alertXslt);
		body = body.replaceAll("java:text", "java:java.text");
		body = body.replaceAll("java:util", "java:java.util");
		
		// CJC: Replace xslt calls that will break xslt translation
		XSLTScanner x = new XSLTScanner(alertProperties, body, true);
		body = x.scan();

		String xmlData = generateFromPFile(alertProperties, alertTestFileInput);		
		StringWriter resultString = new StringWriter();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(new StringReader(body)));
		transformer.transform(new StreamSource(new StringReader(xmlData)), new StreamResult(resultString));
		
		return resultString.toString();
	}
	
	/**
	 * Executes a parsing from a string to replace the util:getCobrandStringValue()
	 * 
	 * @param CBSValue the string that may start with "util:getCobrandableStringValue"
	 * @return The property result of the parsing. No change if string fails CBS test on CBSValue.
	 * 
	 */
	 protected String getCBSProperty(String CBSValue)  throws IOException {
		
	 	 Matcher m = CBSVPattern.matcher(CBSValue);
	 	 String returnedString = new String(CBSValue);
	 	 
	 	 if(m.find()) {
	 		 returnedString = m.group(2); 
	 	 }
		
	 	 return returnedString.toString();
	 }

	/**
	 * Executes a parsing from a PFile to replace the {cobrandId} style variables.
	 * 
	 * @param alertProperties the properties file that contains all the loaded properties
	 * @param dataInputStream input stream of the object to run replacement on.
	 * @return The string result of the parsing
	 * 
	 * @throws IOException indicates an error reading from the input stream
	 */
	protected String generateFromPFile(Properties alertProperties, InputStream dataInputStream) throws IOException {
		StringBuffer resultString = new StringBuffer();
		
		BufferedReader bufferedFileReader = new BufferedReader(new InputStreamReader(dataInputStream));
		
		String nextLine = bufferedFileReader.readLine();
		while(nextLine != null) {
			Matcher matcher = replacePattern.matcher(nextLine);
			while(matcher.matches()) {
				String replacementProperty = matcher.group(2);
				// CJC: if the parameter starts with "util:getCobrandableStringValue", then 
				// getCBSProperty() will return the property string.
				replacementProperty = getCBSProperty(replacementProperty);
				String replacementtext = alertProperties.getProperty(replacementProperty, "[FAIL: "+replacementProperty+"]");
				nextLine = matcher.group(1) + replacementtext + matcher.group(3);
				
				matcher = replacePattern.matcher(nextLine);
			}
			
			resultString.append(nextLine + "\n");
			nextLine = bufferedFileReader.readLine();
		}
		
		
		return resultString.toString();
	}
    
    protected void testDirectoryExistence(String configurationKey, String configurationValue, String configurationFileLocation, String errorMessage) {
        File testFile = new File(configurationValue);
        if(!testFile.exists()) {
            String outputMessage = "\n\nConfiguration Error:\n"
                + "There was a problem with the configuration "
                + "key named [" 
                + configurationKey 
                + "].  The directory that "
                + "is specified ["
                + configurationValue
                + "] does not exist.  Double check the value.  The key can "
                + "either be defined in your configuration property file which "
                + "is classloaded from ["
                + configurationFileLocation
                + "] or passed in on the command line with a -D"
                + configurationKey
                + " command.  The specific use of this key is: "
                + errorMessage
                + "\n\n";
                
            System.out.println(outputMessage);
            throw new IllegalStateException(outputMessage);
        }
    }
	
   
	/**
	 * The main method will create an instance of the Alert Testing Tool
	 * and execute the test method.
	 * 
	 * @param args will be empty
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		AlertTestingTool alertTestingTool = new AlertTestingTool();
		alertTestingTool.test();
		//alertTestingTool.testText();//for text pfile test 
		
		/*String aaa= new AlertTestingTool().getImage("10000004","3");
		System.out.println(aaa);*/
	}
	
}
