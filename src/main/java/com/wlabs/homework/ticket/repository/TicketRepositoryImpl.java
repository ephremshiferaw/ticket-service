package com.wlabs.homework.ticket.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.wlabs.homework.ticket.model.SeatHold;
import com.wlabs.homework.ticket.model.Venue;

/**
 * Repository - Implementation of TicketRepository interface
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
@Component
public class TicketRepositoryImpl implements TicketRepository {

	private Map<Integer, SeatHold> seatHolds = new HashMap<Integer, SeatHold>();
	private Venue venue;

	public TicketRepositoryImpl() {
		super();
		// hard coded for assignment - create and populate test Venue
		venue = new Venue();
		venue.setId(1);
		venue.setNumSeats(100);
	}

	/**
	 * Delete seat hold
	 * 
	 * @param seatHoldKey
	 *            the seat hold key
	 */
	@Override
	public void deleteSeatHold(Integer seatHoldKey) {
		seatHolds.remove(seatHoldKey);
	}

	/**
	 * Find venue by Id
	 *
	 * @param id
	 *            the venue Id
	 * @return the venue
	 */
	@Override
	public Venue findVenueById(long id) {
		return venue;
	}

	/**
	 * Find all seat holds
	 *
	 * @param venueId
	 *            the venue Id
	 * @return Map of all seat holds for a venue
	 */
	@Override
	public Map<Integer, SeatHold> findAllSeatHoldsByVenueId(long venueId) {
		return seatHolds;
	}

	/**
	 * Save seat hold
	 *
	 * @param seatHold
	 *            the seat hold
	 */
	@Override
	public void saveSeatHold(SeatHold seatHold) {
		seatHolds.put(seatHold.getSeat().getNumber(), seatHold);
	}
}