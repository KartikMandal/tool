/*
 * Copyright (c) 2019 Yodlee, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Yodlee, Inc. 
 * Use is subject to license terms.
 */

package com.kartik.aggration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;


/**
 * Class provides the support of Master key based encryption mechanism
 * @author kmandal
 *
 */
public class Encryptor implements StringEncryptor{


	private static final String JCEKS = "JCEKS";

	public static final String FQCN = Encryptor.class.getName();

	/*private static Logger logger = LoggerFactory
			.getLogger(Encryptor.class);*/

	private static String keyStoreFilename = null;//"/opt/ctier/yodleeKeystore";
	private static String keyStorePassword = null;
	public static String ALIAS="master_key";

	public static final byte[] IV = { 88, 15, 19, 125, 112, -96, 93, 102,102, 93, -96, 112, 125, 19, 15, 88 };
	private static Key privateKey;
	private static PublicKey publicKey;	
	
	public static String RSA_ECB_PKCS5 = "RSA/ECB/PKCS1Padding";
	
	//public static String RSA_ECB_PKCS5 = "RSA";

	private static BouncyCastleProvider bcProvider = new BouncyCastleProvider();
	
	static {
		Map<String, String> env = System.getenv();
		String file =env.get("masterKeyConfig");
       //	file=file+"masterKeyConfiguration.properties";
		
		 InputStream is = null;
	        Properties prop = null;
	        try {
	            prop = new Properties();
	            is = new FileInputStream(new File(file));
	            prop.load(is);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		/*ResourceBundle resourceBundle = ResourceBundle
		.getBundle("com.yodlee.masterkey.masterKeyConfiguration");
		if (resourceBundle!=null){
			keyStorePassword=resourceBundle.getString("password");
			if (keyStorePassword==null){
				System.out.println("ERROR: keyStorePassword cannot be null! Pls add masterKeyConfiguration properties.");
			}

		}*/
		keyStorePassword=prop.getProperty("password");
		keyStoreFilename = prop.getProperty("keystoreFilePath");
		ALIAS=prop.getProperty("alias");
		Security.addProvider(bcProvider);
		initService();
	}
	
	/*private static Encryptor instance = new Encryptor();

	private Encryptor() {
	}

	public static Encryptor getInstance() {
		return instance;
	}*/

	/**
	 * Initializes Master key based encryption Service
	 */
	/**
	 * 
	 */
	public static void initService() {
		KeyStore keyStore=null;
		try {
			if (keyStoreFilename==null){
				//keyStoreFilename = System.getProperty("yodlee_keystore_file_name");
				keyStoreFilename = "D:\\yodleeKeystore";
				if (keyStoreFilename==null){
					//logger.warn("ERROR: Please provide key store location full path! Example /opt/ctier/yodleeKeystore");
					System.exit(1);
				}
			}
			
			
			if (keyStoreFilename != null && keyStoreFilename.length()>0) {
				File file = new File(keyStoreFilename);
				keyStore=KeyStore.getInstance(JCEKS);
				if (file.exists()) {
					FileInputStream input = new FileInputStream(file);
					if (keyStore!=null && input!=null && keyStorePassword!=null){
						keyStore.load(input, keyStorePassword.toCharArray());
						input.close();
						privateKey = keyStore.getKey(ALIAS,
								keyStorePassword.toCharArray());

						X509Certificate cert = null;
						cert = (X509Certificate) keyStore.getCertificate(ALIAS);
						if (cert!=null){
							publicKey = cert.getPublicKey();
						}
					}

				} else {
					/*logger.warn("ERROR: No Such file exists "
							+ keyStoreFilename);*/
				}
			} else {
				//logger.warn("ERROR: Please provide correct key store file name! Example /opt/ctier/yodleeKeystore");
				System.exit(1);
			}
		} catch (KeyStoreException keyStoreException) {
			//logger.error("ERROR: "+keyStoreException.getMessage());
		} catch (Exception exception) {
			//logger.error("ERROR: "+exception.getMessage());
		}
	}
	
	/**
	 * Decrypts the value based on Master key
	 * @param cypher
	 * @return
	 */
	@Override
	public String decrypt(String cypher) {
		String decryptedString = null;
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(RSA_ECB_PKCS5);
			if (cipher!=null){
				cipher.init(Cipher.DECRYPT_MODE, transformKey(privateKey,"RSA",
						bcProvider));
				decryptedString = new String(cipher.doFinal(Base64.decodeBase64(cypher.getBytes())));
			}
		} catch (InvalidKeyException invalidKeyException) {
			//logger.warn("ERROR: "+invalidKeyException.getMessage());
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			//logger.warn("ERROR: "+noSuchAlgorithmException.getMessage());
		} catch (NoSuchPaddingException noSuchPaddingException) {
			//logger.warn("ERROR: "+noSuchPaddingException.getMessage());
		} catch (IllegalBlockSizeException illegalBlockSizeException) {
			//logger.warn("ERROR: "+illegalBlockSizeException.getMessage());
		} catch (BadPaddingException badPaddingException) {
			//logger.warn("ERROR: "+badPaddingException.getMessage());
		} catch(InvalidAlgorithmParameterException invalidAlgorithmParameterException){
			//logger.warn("ERROR: "+invalidAlgorithmParameterException.getMessage());
		} catch (Exception exception) {
			//logger.warn("ERROR: "+exception.getMessage());
		} finally {
			cipher = null;
		}
		return decryptedString;
	}

	/**
	 * Encrypts the value mased on master key
	 * @param plainText
	 * @return
	 */
	@Override
	public String encrypt(String plainText) {
		String encryptedString = null;
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(RSA_ECB_PKCS5);
			if (cipher!=null) {
				cipher.init(
						Cipher.ENCRYPT_MODE,
						transformKey(publicKey,"RSA",
								bcProvider));
				//			Hexe
				encryptedString = new String(Base64.encodeBase64(cipher
						.doFinal(plainText.getBytes())));
			}
		} catch (InvalidKeyException invalidKeyException) {
			//logger.warn("ERROR: "+invalidKeyException.getMessage());
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			//logger.warn("ERROR: "+noSuchAlgorithmException.getMessage());
		} catch (NoSuchPaddingException noSuchPaddingException) {
			//logger.warn("ERROR: "+noSuchPaddingException.getMessage());
		} catch (IllegalBlockSizeException illegalBlockSizeException) {
			//logger.warn("ERROR: "+illegalBlockSizeException.getMessage());
		} catch (BadPaddingException badPaddingException) {
			//logger.warn("ERROR: "+badPaddingException.getMessage());
		} catch(InvalidAlgorithmParameterException invalidAlgorithmParameterException){
			//logger.warn("ERROR: "+invalidAlgorithmParameterException.getMessage());
		} catch (Exception exception) {
			//logger.warn("ERROR: "+exception.getMessage());
		} finally {
			cipher = null;
		}
		return encryptedString;
	}

	/**
	 * Transforms the key
	 * @param key
	 * @param algorithm
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	private static Key transformKey(Key key, String algorithm, Provider provider)
			throws Exception {
		Key transformedKey = null;
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm, provider);
		if (keyFactory!=null){
			transformedKey = keyFactory.translateKey(key);
		}
		return transformedKey;
	}
	
	
	public static void main(String[] args){
		Encryptor e=new Encryptor();
		System.out.println(e.encrypt("l1stagging"));
		
		System.out.println();
		System.out.println(e.encrypt("serv4l1qa!"));
		
		//String s="fFAQWsHbkitNtWltjdqvCOxzkxG714VA4byl0CyOBpB4Ck2LfPprKpCV1Y7HreQ48VH16hkio0wvL59cv98Xv9h8Oq0lybxCQ2Xh4C+uTgc7NIhV0XQRY/5tDCbNlnlBU4Da1nWVc/4LFXsxH+A77//4CBiMMhM4oNj51DWuXRE=";

		//System.out.println(e.decrypt(s));
	}
	
}
