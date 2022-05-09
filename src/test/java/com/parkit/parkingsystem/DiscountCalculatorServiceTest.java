package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.parkit.parkingsystem.service.DiscountCalculatorService;


// @ExtendWith(MockitoExtension.class)
public class DiscountCalculatorServiceTest {

  // @Mock
  // private static TicketDAO ticketDAO;

  // @BeforeEach
  // private void setUpPerTest() {}


  @Test
  public void calculateDiscountForRecurringUser() {
    // when(ticketDAO.searchVehicleRegNumber(anyString())).thenReturn(true);
    DiscountCalculatorService discount = new DiscountCalculatorService();
    String vehiculeRegNumber = "ABCDEF";
    double discountCalculated = discount.calculate(vehiculeRegNumber);
    assertEquals(discountCalculated, 0.95);
  }

  @Test
  public void calculateDiscountForNonRecurringUser() {
    // when(ticketDAO.searchVehicleRegNumber(anyString())).thenReturn(false);
    DiscountCalculatorService discount = new DiscountCalculatorService();
    String vehiculeRegNumber = "inexistant";
    double discountCalculated = discount.calculate(vehiculeRegNumber);
    assertEquals(discountCalculated, 1);
  }


}
