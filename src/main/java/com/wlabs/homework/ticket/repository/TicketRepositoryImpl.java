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
		// for homework create and populate test Venue
		venue = new Venue();
		venue.setNumSeats(100);
	}

	/**
	 * Save seat hold
	 *
	 */
	public void deleteSeatHold(Integer seatHoldKey) {
		seatHolds.remove(seatHoldKey);
	}

	/**
	 * Find venue by Id
	 *
	 * @return the venue
	 */
	@Override
	public Venue findVenueById(long id) {
		return venue;
	}

	/**
	 * Find all seat holds
	 *
	 * @return all seat holds
	 */
	@Override
	public Map<Integer, SeatHold> findAllSeatHoldsByVenueId(long venueId) {
		return seatHolds;
	}

	/**
	 * Save seat hold
	 *
	 */
	@Override
	public void saveSeatHold(SeatHold seatHold) {
		seatHolds.put(seatHold.getSeat().getNumber(), seatHold);
	}
}