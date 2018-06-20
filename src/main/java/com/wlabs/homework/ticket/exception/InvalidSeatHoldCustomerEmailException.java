package com.wlabs.homework.ticket.exception;

/**
 * Exception - Customer email does not match the email associated with seat hold request
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public class InvalidSeatHoldCustomerEmailException extends RuntimeException {
	public InvalidSeatHoldCustomerEmailException(String exception) {
		super(exception);
	}
}
