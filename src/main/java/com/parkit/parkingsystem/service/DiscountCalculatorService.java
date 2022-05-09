package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.TicketDAO;

public class DiscountCalculatorService {

  public double calculate(String vehiculeRegNumber) {
    TicketDAO ticketDAO = new TicketDAO();
    boolean exist = ticketDAO.searchVehicleRegNumber(vehiculeRegNumber);
    if (exist) {
      System.out.println(
          "Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
      return 0.95;
    } else {
      return 1;
    }
  }
}
