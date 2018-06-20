package com.wlabs.homework.ticket.model;

import java.time.Instant;
import java.util.List;

/**
 * Model - SeatHold
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public class SeatHold {

	private long id;
	private List<Seat> associatedSeats;
	private boolean confirmed;
	private Customer customer;
	private int holdId;
	private Instant holdTime;
	private String reservationId;
	private Instant reservationTime;
	private Seat seat;
	private long venueId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Seat> getAssociatedSeats() {
		return associatedSeats;
	}

	public void setAssociatedSeats(List<Seat> associatedSeats) {
		this.associatedSeats = associatedSeats;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public int getHoldId() {
		return holdId;
	}

	public void setHoldId(int holdId) {
		this.holdId = holdId;
	}

	public Instant getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(Instant holdTime) {
		this.holdTime = holdTime;
	}

	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}

	public Instant getReservationTime() {
		return reservationTime;
	}

	public void setReservationTime(Instant reservationTime) {
		this.reservationTime = reservationTime;
	}

	public Seat getSeat() {
		return seat;
	}

	public void setSeat(Seat seat) {
		this.seat = seat;
	}

	public long getVenueId() {
		return venueId;
	}

	public void setVenueId(long venueId) {
		this.venueId = venueId;
	}

	@Override
	public String toString() {
		return "SeatHold [id=" + id + ", confirmed=" + confirmed + ", customer=" + customer + ", holdId=" + holdId
				+ ", holdTime=" + holdTime + ", reservationId=" + reservationId + ", reservationTime=" + reservationTime
				+ ", seat=" + seat + ", venueId=" + venueId + "]";
	}
}