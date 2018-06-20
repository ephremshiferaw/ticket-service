package com.wlabs.homework.ticket.model;

/**
 * Model - Seat
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public class Seat {

	private long id;
	private int number;
	private long venueId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public long getVenueId() {
		return venueId;
	}

	public void setVenueId(long venueId) {
		this.venueId = venueId;
	}

	@Override
	public String toString() {
		return "Seat [id=" + id + ", number=" + number + ", venueId=" + venueId + "]";
	}
}