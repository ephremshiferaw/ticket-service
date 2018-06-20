package com.wlabs.homework.ticket.exception;

/**
 * Exception - More seats are requested than are available
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public class InsufficientSeatsAvailableException extends RuntimeException {
	public InsufficientSeatsAvailableException(String exception) {
		super(exception);
	}
}
