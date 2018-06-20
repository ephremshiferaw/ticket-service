package com.wlabs.homework.ticket.exception;

/**
 * Exception - Seat hold has expired and can not be reserved
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public class SeatHoldExpiredException extends RuntimeException {
	public SeatHoldExpiredException(String exception) {
		super(exception);
	}
}
