package com.wlabs.homework.ticket.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wlabs.homework.ticket.model.Customer;
import com.wlabs.homework.ticket.model.Seat;
import com.wlabs.homework.ticket.model.SeatHold;
import com.wlabs.homework.ticket.model.Venue;
import com.wlabs.homework.ticket.repository.TicketRepository;

/**
 * Service - Implementation of TicketService interface
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
@Service
public class TicketServiceImpl implements TicketService {

	private static final Logger log = LogManager.getLogger(TicketServiceImpl.class.getName());

	@Autowired
	private TicketRepository ticketRepository;

	@Value("${seat.hold.archive.seconds:60}")
	int seatHoldArchivalSeconds;

	@Value("${seat.hold.expiration.seconds:60}")
	int seatHoldExpirationSeconds;

	/**
	 * The number of seats in the venue that are neither held nor reserved
	 *
	 * @return the number of tickets available in the venue
	 */
	@Override
	public int numSeatsAvailable() {
		Venue venue = ticketRepository.findVenueById(1);
		Map<Integer, SeatHold> activeSeatHolds = findActiveSeatHolds(1);
		if (log.isDebugEnabled()) {
			log.debug("venueId: " + venue.getId() + " -venueNumSeats: " + venue.getNumSeats() + " -activeSeatHolds: "
					+ activeSeatHolds.size());
		}
		int seatsAvailable = venue.getNumSeats() - activeSeatHolds.size();
		return seatsAvailable;
	}

	/**
	 * Find and hold the best seats available for a customer
	 *
	 * @param numSeats
	 *            the number of seats to find and hold
	 * @param customerEmail
	 *            the unique identifier for the customer
	 * @return a SeatHold identifying the specific seats and related information
	 */
	@Override
	synchronized public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		SeatHold seatHold;
		if (numSeats > numSeatsAvailable()) {
			// InsufficientSeatsAvailableException could be thrown here if permitted
			seatHold = new SeatHold();
			seatHold.setHoldId(-1);
			return seatHold;
		}
		Customer customer = new Customer();
		customer.setEmail(customerEmail);
		Map<Integer, SeatHold> activeSeatHolds = findActiveSeatHolds(1);
		if (isExistingSeatHoldForCustomer(customer, activeSeatHolds)) {
			// ExistingSeatHoldException could be thrown here if permitted
			seatHold = new SeatHold();
			seatHold.setHoldId(-2);
			return seatHold;
		}
		Venue venue = ticketRepository.findVenueById(1);
		List<Seat> bestSeatsAvailable = findBestSeatsAvailable(numSeats, venue.getNumSeats(), activeSeatHolds);
		seatHold = new SeatHold();
		seatHold.setHoldTime(Instant.now());
		seatHold.setCustomer(customer);
		seatHold.setVenueId(1);
		int holdId = new Random().nextInt() & Integer.MAX_VALUE;
		for (Seat seat : bestSeatsAvailable) {
			seatHold.setSeat(seat);
			ticketRepository.saveSeatHold(seatHold);
			seatHold.setHoldId(holdId);
		}
		seatHold.setAssociatedSeats(bestSeatsAvailable);
		if (log.isDebugEnabled()) {
			log.debug("seatHoldId: " + seatHold.getHoldId() + " -customerEmail: " + customerEmail + " -seatsHeld: "
					+ bestSeatsAvailable);
		}
		return seatHold;
	}

	/**
	 * Commit seats held for a specific customer and archive older expired seat
	 * holds
	 *
	 * @param seatHoldId
	 *            the seat hold Id
	 * @param customerEmail
	 *            the email address of the customer to which the seat hold is
	 *            assigned
	 * @return a reservation confirmation code
	 */
	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {
		String reservationId;
		List<SeatHold> customerSeatHolds = findCustomerSeatHoldsByHoldId(seatHoldId);
		if (customerSeatHolds.size() == 0) {
			// SeatHoldNotFoundException could be thrown here if permitted
			return reservationId = "-1";
		}
		if (!customerSeatHolds.get(0).getCustomer().getEmail().equals(customerEmail)) {
			// InvalidSeatHoldCustomerEmailException could be thrown here if permitted
			return reservationId = "-2";
		}
		if (isSeatHoldExpired(customerSeatHolds.get(0))) {
			// SeatHoldExpiredException could be thrown here if permitted
			return reservationId = "-3";
		}
		Instant now = Instant.now();
		reservationId = Integer.toString(new Random().nextInt() & Integer.MAX_VALUE);
		for (SeatHold seatHold : customerSeatHolds) {
			seatHold.setReservationTime(now);
			seatHold.setReservationId(reservationId);
			seatHold.setConfirmed(true);
			ticketRepository.saveSeatHold(seatHold);
		}
		if (log.isDebugEnabled()) {
			log.debug("reservationId: " + reservationId + " -seatHoldId: " + customerSeatHolds.get(0).getHoldId()
					+ " -customerEmail: " + customerEmail + " -numSeatsReserved: " + customerSeatHolds.size());
		}
		return reservationId;
	}

	/**
	 * Returns the active seat holds for a venue
	 *
	 * @param venueId
	 *            the venue Id
	 * @return a Map identifying the SeatHold specific seats and related information
	 */
	private Map<Integer, SeatHold> findActiveSeatHolds(int venueId) {
		Map<Integer, SeatHold> seatHolds = ticketRepository.findAllSeatHoldsByVenueId(1);
		Map<Integer, SeatHold> activeSeatHolds = new HashMap<Integer, SeatHold>();
		List<Integer> archive = new ArrayList<Integer>();
		for (Map.Entry<Integer, SeatHold> seatHold : seatHolds.entrySet()) {
			if (!isSeatHoldExpired(seatHold.getValue()) || isSeatHoldConfirmed(seatHold.getValue())) {
				activeSeatHolds.put(seatHold.getKey(), seatHold.getValue());
			} else {
				// mark for archival
				if (isSeatHoldExpired(seatHold.getValue())) {
					if (isSeatHoldArchivable(seatHold.getValue())) {
						archive.add(seatHold.getKey());
					}
				}
			}
		}
		// archiving older seat holds for analytics/history
		for (Integer seatHoldKey : archive) {
			// for homework just prune repo map
			ticketRepository.deleteSeatHold(seatHoldKey);
		}
		return activeSeatHolds;
	}

	/**
	 * Returns the best seats available
	 *
	 * @param numSeats
	 *            the number of seats requested
	 * @param venueNumSeats
	 *            the number of seats in the venue
	 * @return a List containing the best seats available
	 */
	private List<Seat> findBestSeatsAvailable(int numSeats, int venueNumSeats, Map<Integer, SeatHold> activeSeatHolds) {
		List<Seat> bestSeatsAvailable = new ArrayList<Seat>();
		Seat seat;
		for (int i = 1; i <= venueNumSeats; i++) {
			if (activeSeatHolds.get(i) == null) {
				seat = new Seat();
				seat.setNumber(i);
				seat.setVenueId(1);
				bestSeatsAvailable.add(seat);
			}
			if (bestSeatsAvailable.size() == numSeats) {
				break;
			}
		}
		return bestSeatsAvailable;
	}

	/**
	 * Returns a customer's seat hold information
	 *
	 * @param holdId
	 *            the hold Id
	 * @return a List containing the seat hold information
	 */
	private List<SeatHold> findCustomerSeatHoldsByHoldId(int holdId) {
		Map<Integer, SeatHold> seatHolds = ticketRepository.findAllSeatHoldsByVenueId(1);
		List<SeatHold> customerSeatHolds = new ArrayList<SeatHold>();
		for (Map.Entry<Integer, SeatHold> seatHold : seatHolds.entrySet()) {
			if (seatHold.getValue().getHoldId() == holdId) {
				customerSeatHolds.add(seatHold.getValue());
			}
		}
		return customerSeatHolds;
	}

	/**
	 * Returns boolean indicating if customer has existing active seat hold
	 *
	 * @param customer
	 *            the Customer
	 * @param activeSeatHolds
	 *            the active seat holds for a venue
	 * @return a boolean indicating if customer has a seat hold
	 */
	private boolean isExistingSeatHoldForCustomer(Customer customer, Map<Integer, SeatHold> activeSeatHolds) {
		for (Map.Entry<Integer, SeatHold> activeSeatHold : activeSeatHolds.entrySet()) {
			if (activeSeatHold.getValue().getCustomer().getEmail().equals(customer.getEmail())) {
				if (!activeSeatHold.getValue().isConfirmed()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns boolean indicating if the seat hold should be archived
	 *
	 * @param seatHold
	 *            the SeatHold
	 * @return a boolean indicating if the seat hold should be archived
	 */
	private boolean isSeatHoldArchivable(SeatHold seatHold) {
		Instant now = Instant.now();
		Duration d = Duration.between(seatHold.getHoldTime(), now);
		return d.getSeconds() > seatHoldArchivalSeconds ? true : false;
	}

	/**
	 * Returns boolean indicating if the seat hold is confirmed
	 *
	 * @param seatHold
	 *            the SeatHold
	 * @return a boolean indicating if the seat hold is confirmed
	 */
	private boolean isSeatHoldConfirmed(SeatHold seatHold) {
		return seatHold.isConfirmed() ? true : false;
	}

	/**
	 * Returns boolean indicating if the seat hold is expired
	 *
	 * @param seatHold
	 *            the SeatHold
	 * @return a boolean indicating if the seat hold is expired
	 */
	private boolean isSeatHoldExpired(SeatHold seatHold) {
		Instant now = Instant.now();
		Duration d = Duration.between(seatHold.getHoldTime(), now);
		return d.getSeconds() > seatHoldExpirationSeconds ? true : false;
	}
}