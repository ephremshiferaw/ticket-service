package com.wlabs.homework.ticket.exception;

/**
 * Exception - Existing seat hold for the customer email address
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public class ExistingSeatHoldException extends RuntimeException {
	public ExistingSeatHoldException(String exception) {
		super(exception);
	}
}
