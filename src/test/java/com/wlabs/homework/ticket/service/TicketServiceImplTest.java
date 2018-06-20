package com.wlabs.homework.ticket.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.wlabs.homework.ticket.model.Customer;
import com.wlabs.homework.ticket.model.Seat;
import com.wlabs.homework.ticket.model.SeatHold;
import com.wlabs.homework.ticket.model.Venue;
import com.wlabs.homework.ticket.repository.TicketRepository;

/**
 * JUnit - Tests for the TicketService
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
@RunWith(SpringRunner.class)
public class TicketServiceImplTest {

	@TestConfiguration
	static class TicketServiceImplTestContextConfiguration {
		@Bean
		public TicketService ticketService() {
			return new TicketServiceImpl();
		}
	}

	@Autowired
	private TicketService ticketService;

	@MockBean
	private TicketRepository ticketRepository;

	@Value("${seat.hold.archive.seconds:60}")
	int seatHoldArchivalSeconds;
	
	@Value("${seat.hold.expiration.seconds:60}")
	int seatHoldExpirationSeconds;

	Venue venue = new Venue();

	@Before
	public void setUp() {
		venue.setNumSeats(250);
	}

	@Test
	public void whenNoSeatHoldsActive_thenAllSeatsShouldBeAvailable() {
		Map<Integer, SeatHold> seatHolds = new HashMap<Integer, SeatHold>();
		Mockito.when(ticketRepository.findVenueById(1)).thenReturn(venue);
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		int numSeatsAvailable = ticketService.numSeatsAvailable();
		assertThat(numSeatsAvailable).isEqualTo(250);
	}

	@Test
	public void whenThreeSeatHoldsActive_thenThreeSeatsShouldBeUnavailable() {
		Map<Integer, SeatHold> seatHolds = populateSeatHoldThreeActive(new HashMap<Integer, SeatHold>());
		Mockito.when(ticketRepository.findVenueById(1)).thenReturn(venue);
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		int numSeatsAvailable = ticketService.numSeatsAvailable();
		assertThat(numSeatsAvailable).isEqualTo(247);
	}

	@Test
	public void whenOneOfThreeSeatHoldsExpired_thenTwoSeatsShouldBeUnavailable() {
		Map<Integer, SeatHold> seatHolds = populateSeatHoldOneOfThreeExpired(new HashMap<Integer, SeatHold>());
		Mockito.when(ticketRepository.findVenueById(1)).thenReturn(venue);
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		int numSeatsAvailable = ticketService.numSeatsAvailable();
		assertThat(numSeatsAvailable).isEqualTo(248);
	}

	@Test
	public void whenMoreSeatsRequestThanAvailable_thenErrorCodeShouldBeReturned() {
		Map<Integer, SeatHold> seatHolds = populateSeatHoldOne(new HashMap<Integer, SeatHold>());
		Mockito.when(ticketRepository.findVenueById(1)).thenReturn(venue);
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		SeatHold seatHold = ticketService.findAndHoldSeats(251, "wlabs.tester@wlabs.com");
		assertThat(seatHold.getHoldId()).isEqualTo(-1);
	}

	@Test
	public void whenCustomerHasActiveSeatHold_thenErrorCodeShouldBeReturned() {
		Map<Integer, SeatHold> seatHolds = populateSeatHoldOne(new HashMap<Integer, SeatHold>());
		Mockito.when(ticketRepository.findVenueById(1)).thenReturn(venue);
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		SeatHold seatHold = ticketService.findAndHoldSeats(3, "1wlabs.tester@wlabs.com");
		assertThat(seatHold.getHoldId()).isEqualTo(-2);
	}

	@Test
	public void whenFirstSeatHeld_thenNextThreeSeatsShouldBeBestAvailable() {
		Map<Integer, SeatHold> seatHolds = populateSeatHoldOne(new HashMap<Integer, SeatHold>());
		Mockito.when(ticketRepository.findVenueById(1)).thenReturn(venue);
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		SeatHold seatHold = ticketService.findAndHoldSeats(3, "wlabs.tester@wlabs.com");
		assertThat(seatHold.getHoldId()).isGreaterThan(0);
		assertThat(seatHold.getCustomer().getEmail()).isEqualTo("wlabs.tester@wlabs.com");
		assertThat(seatHold.getSeat().getNumber()).isEqualTo(4);
	}

	@Test
	public void whenSecondsAndFourthSeatHeld_thenFifthSeatShouldBeLastBestAvailable() {
		Map<Integer, SeatHold> seatHolds = populateSeatHoldSeatsTwoAndFour(new HashMap<Integer, SeatHold>());
		Mockito.when(ticketRepository.findVenueById(1)).thenReturn(venue);
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		SeatHold seatHold = ticketService.findAndHoldSeats(3, "wlabs.tester@wlabs.com");
		assertThat(seatHold.getHoldId()).isGreaterThan(0);
		assertThat(seatHold.getCustomer().getEmail()).isEqualTo("wlabs.tester@wlabs.com");
		assertThat(seatHold.getSeat().getNumber()).isEqualTo(5);
	}

	@Test
	public void whenValidSeatHoldReserved_thenReservationIdIsReturned() {
		Map<Integer, SeatHold> seatHolds = populateSeatHoldOne(new HashMap<Integer, SeatHold>());
		Mockito.when(ticketRepository.findVenueById(1)).thenReturn(venue);
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		String reservationId = ticketService.reserveSeats(123, "1wlabs.tester@wlabs.com");
		assertThat(reservationId).isNotNull();
		assertThat(reservationId).isNotEqualTo("-1");
		assertThat(reservationId).isNotEqualTo("-2");
		assertThat(reservationId).isNotEqualTo("-3");
	}

	@Test
	public void whenHoldIdNotFoundDuringSeatHoldReserve_thenErrorCodeShouldBeReturne() {
		String reservationId = ticketService.reserveSeats(987, "123wlabs.tester@wlabs.com");
		assertThat(reservationId).isNotNull();
		assertThat(reservationId).isEqualTo("-1");
	}

	@Test
	public void whenEmailMismatchDuringSeatHoldReserve_thenErrorCodeShouldBeReturne() {
		Map<Integer, SeatHold> seatHolds = populateSeatHoldOne(new HashMap<Integer, SeatHold>());
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		String reservationId = ticketService.reserveSeats(123, "123wlabs.tester@wlabs.com");
		assertThat(reservationId).isNotNull();
		assertThat(reservationId).isEqualTo("-2");
	}

	@Test
	public void whenExpiredHoldDuringSeatHoldReserved_thenErrorCodeShouldBeReturne() {
		Map<Integer, SeatHold> seatHolds = populateSeatHoldOneExpired(new HashMap<Integer, SeatHold>());
		Mockito.when(ticketRepository.findAllSeatHoldsByVenueId(1)).thenReturn(seatHolds);
		String reservationId = ticketService.reserveSeats(123, "5wlabs.tester@wlabs.com");
		assertThat(reservationId).isNotNull();
		assertThat(reservationId).isEqualTo("-3");
	}

	/**
	 * Test data population helper
	 * 
	 */
	private Map<Integer, SeatHold> populateSeatHoldThreeActive(Map<Integer, SeatHold> seatHolds) {
		SeatHold seatHold;
		Customer customer;
		Seat seat;
		for (int i = 1; i < 4; i++) {
			seatHold = new SeatHold();
			customer = new Customer();
			seatHold.setCustomer(customer);
			customer.setEmail(i + "wlabs.tester@wlabs.com");
			seat = new Seat();
			seat.setNumber(i);
			seat.setVenueId(1);
			seatHold.setSeat(seat);
			seatHold.setHoldTime(Instant.now().minusSeconds(i + 1));
			seatHolds.put(seat.getNumber(), seatHold);
		}
		return seatHolds;
	}

	/**
	 * Test data population helper
	 * 
	 */
	private Map<Integer, SeatHold> populateSeatHoldOneOfThreeExpired(Map<Integer, SeatHold> seatHolds) {
		SeatHold seatHold;
		Customer customer;
		Seat seat;
		for (int i = 1; i < 4; i++) {
			seatHold = new SeatHold();
			customer = new Customer();
			seatHold.setCustomer(customer);
			customer.setEmail(i + "wlabs.tester@wlabs.com");
			seat = new Seat();
			seat.setNumber(i);
			seat.setVenueId(1);
			seatHold.setSeat(seat);
			seatHold.setHoldTime(Instant.now().minusSeconds(i + 58));
			seatHolds.put(seat.getNumber(), seatHold);
		}
		return seatHolds;
	}

	/**
	 * Test data population helper
	 * 
	 */
	private Map<Integer, SeatHold> populateSeatHoldOne(Map<Integer, SeatHold> seatHolds) {
		SeatHold seatHold;
		Customer customer;
		Seat seat;
		for (int i = 1; i < 2; i++) {
			seatHold = new SeatHold();
			seatHold.setHoldId(123);
			customer = new Customer();
			seatHold.setCustomer(customer);
			customer.setEmail(i + "wlabs.tester@wlabs.com");
			seat = new Seat();
			seat.setNumber(i);
			seat.setVenueId(1);
			seatHold.setSeat(seat);
			seatHold.setHoldTime(Instant.now().minusSeconds(i + 1));
			seatHolds.put(seat.getNumber(), seatHold);
		}
		return seatHolds;
	}

	/**
	 * Test data population helper
	 * 
	 */
	private Map<Integer, SeatHold> populateSeatHoldSeatsTwoAndFour(Map<Integer, SeatHold> seatHolds) {
		SeatHold seatHold;
		Customer customer;
		Seat seat;
		for (int i = 1; i <= 3; i += 2) {
			seatHold = new SeatHold();
			customer = new Customer();
			seatHold.setCustomer(customer);
			customer.setEmail(i + "wlabs.tester@wlabs.com");
			seat = new Seat();
			seat.setNumber(i + 1);
			seat.setVenueId(1);
			seatHold.setSeat(seat);
			seatHold.setHoldTime(Instant.now().minusSeconds(i + 1));
			seatHolds.put(seat.getNumber(), seatHold);
		}
		return seatHolds;
	}

	/**
	 * Test data population helper
	 * 
	 */
	private Map<Integer, SeatHold> populateSeatHoldOneExpired(Map<Integer, SeatHold> seatHolds) {
		SeatHold seatHold = new SeatHold();
		seatHold.setHoldId(123);
		Customer customer = new Customer();
		seatHold.setCustomer(customer);
		customer.setEmail("5wlabs.tester@wlabs.com");
		Seat seat = new Seat();
		seat.setNumber(1);
		seat.setVenueId(1);
		seatHold.setSeat(seat);
		seatHold.setHoldTime(Instant.now().minusSeconds(600));
		seatHolds.put(seat.getNumber(), seatHold);
		return seatHolds;
	}
}