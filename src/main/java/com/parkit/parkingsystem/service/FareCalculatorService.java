package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.Instant;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

  public void calculateFare(Ticket ticket) {
    if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
      throw new IllegalArgumentException(
          "Out time provided is incorrect:" + ticket.getOutTime().toString());
    }

    Instant inHour = ticket.getInTime();
    Instant outHour = ticket.getOutTime();

    long duration = Duration.between(inHour, outHour).toMinutes();

    if (duration < 31) {
      duration = 0;
    }
    switch (ticket.getParkingSpot().getParkingType())

    {
      case CAR: {
        ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR / 60);
        break;
      }
      case BIKE: {
        ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR / 60);
        break;
      }
      default:
        throw new IllegalArgumentException("Unkown Parking Type");
    }
  }
}
