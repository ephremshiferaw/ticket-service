package com.wlabs.homework.ticket.repository;

import java.util.Map;

import com.wlabs.homework.ticket.model.SeatHold;
import com.wlabs.homework.ticket.model.Venue;

/**
 * Repository - Interface for TicketRepository implementations
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public interface TicketRepository {

	/**
	 * Delete seat hold
	 * 
	 */
	public void deleteSeatHold(Integer seatHoldKey); 
	
	/**
	 * Find venue by Id
	 *
	 * @return the venue
	 */
	public Venue findVenueById(long id);

	/**
	 * Find all seat holds
	 *
	 * @return all seat holds
	 */
	public Map<Integer, SeatHold> findAllSeatHoldsByVenueId(long id);

	/**
	 * Save seat hold
	 *
	 */
	public void saveSeatHold(SeatHold seatHold);
}