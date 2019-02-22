package com.ventia.homework.exception;
/***
 * 
 * @author Cornelius Franken
 * This exception is thrown when there is a problem access any Azure AD Servers.
 * For example when trying top retrieve the public keys from the key list.
 */
public class AzureADConnectionException extends Exception {

	public AzureADConnectionException() {
		super();
	}

	public AzureADConnectionException(String message) {
		super(message);

	}


}
