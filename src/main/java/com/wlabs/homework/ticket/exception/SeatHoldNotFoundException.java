package com.wlabs.homework.ticket.exception;

/**
 * Exception - Seat hold is not found
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public class SeatHoldNotFoundException extends RuntimeException {
	public SeatHoldNotFoundException(String exception) {
		super(exception);
	}
}
