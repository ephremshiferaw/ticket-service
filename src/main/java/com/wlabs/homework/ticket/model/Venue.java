package com.wlabs.homework.ticket.model;

/**
 * Model - Venue
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public class Venue {

	private long id;
	private String name;
	private int numSeats;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumSeats() {
		return numSeats;
	}

	public void setNumSeats(int numSeats) {
		this.numSeats = numSeats;
	}

	@Override
	public String toString() {
		return "Venue [id=" + id + ", name=" + name + ", numSeats=" + numSeats + "]";
	}
}