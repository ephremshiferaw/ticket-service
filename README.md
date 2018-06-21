# Ticket Service Assignment

Design and implement a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.

In addition to unit tests a command line client application (TicketServiceCLIApplication) is included. Please find instructions below concerning usage.


## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - The framework
* [Maven](https://maven.apache.org/) - Dependency management


## Requirements
* Java version 1.8
* Maven version 3.3
* Git 2.5 (if cloning)


## Downloading

To git clone execute the following:

$ git clone https://github.com/zenkissel/ticket-service ticket-service

To download a ZIP please click 'Clone or download' and then 'Download ZIP' here:

https://github.com/zenkissel/ticket-service


## Building and Packaging

Execute the following from the project root directory (ticket-service/) to clean, compile, execute tests, and generate a jar:

$ mvn clean package


## Running the tests

Execute the following from the project root directory (ticket-service/) to run the tests:

$ mvn test


## Running the Command Line Application

There are two options:

Execute the following from the project root directory (ticket-service/):

$ mvn spring-boot:run

Execute the following from the project root directory (ticket-service/):

$ java -jar target/ticket-service-0.0.1-SNAPSHOT.jar


## Configuration

Seat hold expiration and archival setting are configured in application.properties.

By default seat holds will expire after 60 seconds and seat holds will be archived (for purpose of assignment removed from repository) after 300 seconds.


## Logging

Apache Log4j 2 is configured and used. With the default configuration the following log file will be created, populated, and managed:

ticket-service/logs/ticket-service-%d{yyyy-MM-dd}-%i.log


## Sequence Diagrams
![sequenceDiagram](https://github.com/zenkissel/ticket-service-doc/blob/master/diagram/sequence/img/numSeatsAvailable.png)


![sequenceDiagram](https://github.com/zenkissel/ticket-service-doc/blob/master/diagram/sequence/img/findAndHoldSeats.png)


![sequenceDiagram](https://github.com/zenkissel/ticket-service-doc/blob/master/diagram/sequence/img/reserveSeats.png)


## Assumptions
1. The best available seat is determined by the seat number. The lower the seat number the better the seat. 
2. A seat hold for multiple seats does not guarantee the seats are next to each other or in the same row.
3. A customer can only request a single seat hold at a time. If the seats included in the seat hold are reserved or the seat hold has expired the customer may request a new seat hold.
4. During reservation the seats identified during the seat hold process are the seats that are reserved. If better seats have opened up since the seat hold was created this is not taken into consideration.
5. The service does not need to validate input data. The CLI contains simple input data validation. Any future controller exposing the service would perform the required input data validation.
6. The provided interface was not to be modified.


## Notes
1. As the provided interface does not include custom checked Exceptions, they could not be introduced in the service as that would result in a more restrictive implementing class. That plus the lack of a standard response class that includes an error code required one-off workarounds to communicate the type of error to the CLI. Please note the exceptions which would have been used can be found in the exceptions package.
2. As the provided interface does not include venueId as a parameter for numSeatsAvailable, a test venueId is hard coded in the service. Only a single test venue is supported.
3. The id attributes on Model classes are not relevant for Map based repository and are place holders for future persistence requirements.


## Future Considerations
1. Given the design the service could be exposed via a REST API or MVC Controller with relative ease.
2. A JPA repository (or repositories) or a different type of data access class could be introduced to replace the included simple Map based repository.
3. The synchronized findAndHoldSeats method could be modified to adopt a better performing approach when under load. Relying on transactions and underlying database support for atomic transactions could be considered. Java locks might also provide a way to improve.


## Author

**Michael Kissel** - [zenkissel](https://github.com/zenkissel)


## License

Public domain


## Acknowledgments

Assignment creator(s)