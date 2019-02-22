package com.ventia.homework.exception;


/***
 * 
 * @author Cornelius Franken
 * This exception is thrown in the following situation
 * 1. When the token cannot be decoded into a  JWT token. 
 * 2. When the token does not contain claims that are mandatory in our requirements.
 * 3. When the token cannot be verified with the public key
 * The message would contain which one of the above scenarios are valid when thrown.
 * 
 */
public class InvalidTokenException extends Exception{

	

	public InvalidTokenException() {
		super();
	}
	public InvalidTokenException(String message) {
		super(message);
		
	}

}
