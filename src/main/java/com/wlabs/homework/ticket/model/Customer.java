package com.wlabs.homework.ticket.model;

/**
 * Model - Customer
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */
public class Customer {

	private long id;
	private String email;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", email=" + email + "]";
	}
}