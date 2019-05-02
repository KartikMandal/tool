package com.kartik.exception;


/**
 * The <code>InvalidEncodingException</code> exception is thrown
 * whenever we fail to decode data that was encoded using any of the supported encoding methods
 * such as <code>Base64</code> or <code>Hex</code>.
 */
public class InvalidCertificateTypeException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidCertificateTypeException(String info) {

        super(info);
    }

}

