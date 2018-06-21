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
	 * @param seatHoldKey
	 *            the seat hold key
	 */
	public void deleteSeatHold(Integer seatHoldKey); 
	
	/**
	 * Find venue by Id
	 *
	 * @param id
	 *            the venue Id
	 * @return the venue
	 */
	public Venue findVenueById(long id);

	/**
	 * Find all seat holds
	 *
	 * @param venueId
	 *            the venue Id
	 * @return Map of all seat holds for a venue
	 */
	public Map<Integer, SeatHold> findAllSeatHoldsByVenueId(long venueId);

	/**
	 * Save seat hold
	 *
	 * @param seatHold
	 *            the seat hold
	 */
	public void saveSeatHold(SeatHold seatHold);
}