package com.kartik.exception;


/**
 * The <code>InvalidBase64EncodingException</code> exception is thrown
 * whenever we fail to decode data that was assumed to be in Base64 format.
 */
public class InvalidBase64EncodingException extends InvalidCertificateTypeException
{
	private static final long serialVersionUID = 1L;

    public InvalidBase64EncodingException(String info) {

        super(info);
    }

}

