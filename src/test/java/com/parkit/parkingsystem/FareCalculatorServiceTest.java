package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.Instant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

  private static FareCalculatorService fareCalculatorService;
  private Ticket ticket;

  @BeforeAll
  private static void setUp() {
    fareCalculatorService = new FareCalculatorService();
  }

  @BeforeEach
  private void setUpPerTest() {
    ticket = new Ticket();
  }

  @Test
  public void calculateFareCar() {
    Instant inTime = Instant.now();
    inTime = inTime.plusMillis(-(60 * 60 * 1000));
    Instant outTime = Instant.now();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setPrice(1); // no Discount
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareBike() {
    Instant inTime = Instant.now();
    inTime = inTime.plusMillis(-(60 * 60 * 1000));
    Instant outTime = Instant.now();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    ticket.setPrice(1); // no Discount
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareUnkownType() {
    Instant inTime = Instant.now();
    inTime = inTime.plusMillis(-60 * 60 * 1000);
    Instant outTime = Instant.now();
    ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
    ticket.setPrice(1); // no Discount
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  @Test
  public void calculateFareBikeWithFutureInTime() {
    Instant inTime = Instant.now();
    inTime = inTime.plusMillis(60 * 60 * 1000);
    Instant outTime = Instant.now();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    ticket.setPrice(1); // no Discount
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  @Test
  public void calculateFareBikeWithLessThanOneHourParkingTime() {
    Instant inTime = Instant.now();
    inTime = inTime.plusMillis(-(45 * 60 * 1000));// 45 minutes parking time
    // should give 3/4th
    // parking fare
    Instant outTime = Instant.now();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    ticket.setPrice(1); // no Discount
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  public void calculateFareCarWithLessThanOneHourParkingTime() {
    Instant inTime = Instant.now();
    inTime = inTime.plusMillis(-(45 * 60 * 1000));// 45 minutes parking time
    // should give 3/4th
    // parking fare
    Instant outTime = Instant.now();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setPrice(1); // no Discount
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  public void calculateFareCarWithLessThanOneHourParkingTimeWithDiscount() {
    Instant inTime = Instant.now();
    inTime = inTime.plusMillis(-(45 * 60 * 1000));// 45 minutes parking time
    // should give 3/4th
    // parking fare
    Instant outTime = Instant.now();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setPrice(0.95); // 5% Discount
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR * 0.95), ticket.getPrice());
  }

  @Test
  public void calculateFareCarWithMoreThanADayParkingTime() {
    Instant inTime = Instant.now();
    inTime = inTime.plusMillis(-(24 * 60 * 60 * 1000));// 24 hours parking
    // time should give
    // 24 * parking fare
    // per hour
    Instant outTime = Instant.now();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setPrice(1); // no Discount
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
  }

  @Test
  public void calculateFareCarWithLessThan30MinutesParkingTime() {
    Instant inTime = Instant.now();
    inTime = inTime.plusMillis(-(24 * 60 * 1000));// 24 minutes parking
    // should give free (0)
    // parking fare
    Instant outTime = Instant.now();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setPrice(1); // no Discount
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertEquals(0, ticket.getPrice());
  }
}
