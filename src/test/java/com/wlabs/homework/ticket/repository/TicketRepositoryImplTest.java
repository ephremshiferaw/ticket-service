package com.wlabs.homework.ticket.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.wlabs.homework.ticket.model.Seat;
import com.wlabs.homework.ticket.model.SeatHold;

/**
 * JUnit - Tests for the TicketRepository
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
@RunWith(SpringRunner.class)
public class TicketRepositoryImplTest {

	@TestConfiguration
	static class TicketRepositoryImplTestContextConfiguration {
		@Bean
		public TicketRepository ticketRepository() {
			return new TicketRepositoryImpl();
		}
	}

	@Autowired
	private TicketRepository ticketRepository;

	@Test
	public void whenSaveSeatHold_thenSeatHoldSaved() {
		SeatHold seatHold = new SeatHold();
		Seat seat = new Seat();
		seat.setNumber(15);
		seatHold.setSeat(seat);
		ticketRepository.saveSeatHold(seatHold);
		assertThat(ticketRepository.findAllSeatHoldsByVenueId(1).containsKey(15)).isEqualTo(true);
		assertThat(ticketRepository.findAllSeatHoldsByVenueId(1).containsKey(14)).isEqualTo(false);
	}

	@Test
	public void whenFindAllSeatHoldsByVenueId_thenAllSeatHoldsReturned() {
		SeatHold seatHold;
		for (int i = 1; i <= 100; i++) {
			seatHold = new SeatHold();
			Seat seat = new Seat();
			seat.setNumber(i);
			seatHold.setSeat(seat);
			ticketRepository.saveSeatHold(seatHold);
		}
		assertThat(ticketRepository.findAllSeatHoldsByVenueId(1).size()).isEqualTo(100);
		assertThat(ticketRepository.findAllSeatHoldsByVenueId(1).containsKey(100)).isEqualTo(true);
		assertThat(ticketRepository.findAllSeatHoldsByVenueId(1).containsKey(101)).isEqualTo(false);
	}

	@Test
	public void whenDeleteSeatHold_thenSeatHoldDeleted() {
		SeatHold seatHold = new SeatHold();
		Seat seat = new Seat();
		seat.setNumber(10);
		seatHold.setSeat(seat);
		ticketRepository.saveSeatHold(seatHold);
		assertThat(ticketRepository.findAllSeatHoldsByVenueId(1).containsKey(10)).isEqualTo(true);
		ticketRepository.deleteSeatHold(10);
		assertThat(ticketRepository.findAllSeatHoldsByVenueId(1).containsKey(10)).isEqualTo(false);
	}

}