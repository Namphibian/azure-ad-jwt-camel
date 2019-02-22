package com.ventia.homework.exception;
/***
 * 
 * @author Cornelius Franken
 * This exception is thrown when we cannot find a public key to verify a signature.
 */
public class InvalidTokenPublicKeyException extends Exception{

	public InvalidTokenPublicKeyException() {
		super();
	}

	public InvalidTokenPublicKeyException(String message) {
		super(message);
		
	}
}
