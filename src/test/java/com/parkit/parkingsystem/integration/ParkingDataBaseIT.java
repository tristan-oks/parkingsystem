package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  private static ParkingSpotDAO parkingSpotDAO;
  private static TicketDAO ticketDAO;
  private static DataBasePrepareService dataBasePrepareService;

  @Mock
  private static InputReaderUtil inputReaderUtil;

  @BeforeAll
  private static void setUp() throws Exception {
    parkingSpotDAO = new ParkingSpotDAO();
    parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    ticketDAO = new TicketDAO();
    ticketDAO.dataBaseConfig = dataBaseTestConfig;
    dataBasePrepareService = new DataBasePrepareService();
  }

  @BeforeEach
  private void setUpPerTest() throws Exception {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    dataBasePrepareService.clearDataBaseEntries();
  }

  @AfterAll
  private static void tearDown() {}

  @Test
  // test Parking A Car;
  public void testParkingACar() throws Exception {
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();

    Ticket ticket = ticketDAO.getTicket("ABCDEF");
    assertThat(ticket.getVehicleRegNumber()).isEqualTo("ABCDEF");
    int nextSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
    assertThat(ticket.getParkingSpot().getId()).isNotEqualTo(nextSlot);
    assertThat(ticket.getParkingSpot().isAvailable()).isEqualTo(false);
  }

  @Test
  // test Exiting A Car after 1 hour with no discount;
  public void testParkingLotExitWithNoDiscount() throws Exception {
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    parkingService.processIncomingVehicle();
    Ticket ticket = ticketDAO.getTicket("ABCDEF");
    ticket.setInTime(Instant.now().plusMillis(-60 * 60 * 1000));
    ticketDAO.updateTicketInTime(ticket);

    parkingService.processExitingVehicle();
    ticket = ticketDAO.getTicket("ABCDEF"); // With no discount
    long duration = Duration.between(ticket.getOutTime(), Instant.now()).toMinutes();
    assertEquals(duration, 0);
    assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR * 1, 0.05);
    // added 1 minute marging
  }

  @Test
  // test Exiting A Car after 1 hour with discount;
  public void testParkingLotExitWithDiscount() throws Exception {
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

    Ticket oldTicket = new Ticket();
    oldTicket.setInTime(Instant.now());
    oldTicket.setOutTime(Instant.now());
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
    oldTicket.setParkingSpot(parkingSpot);
    oldTicket.setVehicleRegNumber("ABCDEF");
    ticketDAO.saveTicket(oldTicket); // create an old ticket to become recurrent user

    parkingService.processIncomingVehicle();
    Ticket ticket = ticketDAO.getTicket("ABCDEF");
    ticket.setInTime(Instant.now().plusMillis(-60 * 60 * 1000));
    ticketDAO.updateTicketInTime(ticket);

    parkingService.processExitingVehicle();
    ticket = ticketDAO.getTicket("ABCDEF"); // With 5% discount
    long duration = Duration.between(ticket.getOutTime(), Instant.now()).toMinutes();
    assertEquals(duration, 0);
    assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR * 0.95, 0.05);
    // added 1 minute marging
  }
}
