package com.wlabs.homework.ticket;

import java.util.Scanner;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.wlabs.homework.ticket.model.Seat;
import com.wlabs.homework.ticket.model.SeatHold;
import com.wlabs.homework.ticket.service.TicketService;

/**
 * Application - Spring Boot CLI application for Ticket service usage
 * 
 * @author <a href="mailto:michael.kissel@gmail.com">Michael Kissel</a>
 */

@SpringBootApplication
public class TicketServiceCLIApplication implements CommandLineRunner {

	@Autowired
	private TicketService ticketService;

	private final static Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	public static void main(String[] args) throws Exception {
		SpringApplication app = new SpringApplication(TicketServiceCLIApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		String input = "";
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("\n*************************************************************");
			System.out.println("** Welcome to the Simple Ticket Service CLI.");
			System.out.println("** Input 'q' to quit at any time.");
			System.out.println("*************************************************************");
			while (!input.equals("q")) {
				System.out.println(generateMenu(input));
				input = scanner.nextLine();
				switch (input) {
				case "1":
					System.out.println(
							wrap("Here are the number of seats available: " + ticketService.numSeatsAvailable()));
					break;
				case "2":
					System.out.print("Please enter your email address: ");
					String customerEmail = scanner.nextLine();
					while (!p.matcher(customerEmail).find()) {
						System.out.println("Please enter a valid email address: ");
						customerEmail = scanner.next();
					}
					System.out.print("Please enter the number of seats you would like to hold: ");
					while (!scanner.hasNextInt()) {
						System.out.println("Please enter a valid number: ");
						scanner.next();
					}
					int numSeats = scanner.nextInt();
					SeatHold seatHold = ticketService.findAndHoldSeats(numSeats, customerEmail);
					if (seatHold.getHoldId() == -1) {
						System.out.println(wrap(
								"Not enough seats are available.\nHere are the number of seats currently available: "
										+ ticketService.numSeatsAvailable()));
					} else if (seatHold.getHoldId() == -2) {
						System.out.println(wrap("A customer can only have one active seat hold request at a time."));
					} else {
						StringBuilder seats = new StringBuilder();
						for (Seat seat : seatHold.getAssociatedSeats()) {
							seats.append(seat.getNumber());
							seats.append(" ");
						}
						System.out.println(wrap("The following seat numbers have been held: " + seats.toString()
								+ "\nHere is the seat hold Id #: " + seatHold.getHoldId()));
					}
					break;
				case "3":
					System.out.print("Please enter your email address: ");
					customerEmail = scanner.nextLine();
					while (!p.matcher(customerEmail).find()) {
						System.out.println("Please enter a valid email address: ");
						customerEmail = scanner.next();
					}
					System.out.print("Please enter the seat hold Id # you would like to confirm: ");
					while (!scanner.hasNextInt()) {
						System.out.println("Please enter a valid number: ");
						scanner.next();
					}
					int seatHoldId = scanner.nextInt();
					String reservationId = ticketService.reserveSeats(seatHoldId, customerEmail);
					if (reservationId.equals("-1")) {
						System.out.print(wrap("The seat hold Id # was not found: " + seatHoldId));
					} else if (reservationId.equals("-2")) {
						System.out.print(wrap("The email address " + customerEmail
								+ " did not match for seat hold request: " + seatHoldId));
					} else if (reservationId.equals("-3")) {
						System.out.print(wrap("The seat hold has expired: " + seatHoldId
								+ ". Please create and then confirm a new seat hold to complete a reservation."));
					} else {
						System.out.println(wrap("Your reservation has been confirmed!\nHere is the reserveration Id #: "
								+ reservationId));
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Unexpected error. Exiting...");
			e.printStackTrace(System.out);
			System.exit(0);
		}
	}

	/**
	 * Helper method to generate CLI menu
	 * 
	 * @param input
	 *            the CLI input
	 * @return the String menu
	 */
	private static final String generateMenu(String input) {
		StringBuilder sb = new StringBuilder();
		if (input.equals("") || input.equals("1")) {
			sb.append("Please select from the following:\n\n");
			sb.append("1.) View number of available seats\n");
			sb.append("2.) Create a seat hold request\n");
			sb.append("3.) Reserve seats by confirming an existing seat hold\n\n");
			sb.append("Input 'q' to quit at any time.");
		}
		return sb.toString();
	}

	/**
	 * Helper method to wrap String input with asterisks for CLI presentation
	 * 
	 * @param input
	 *            the String input to be wrapped
	 * @return the wrapped String value
	 */
	private static final String wrap(String input) {
		return "\n*****************************************************\n" + input
				+ "\n*****************************************************\n";
	}
}
