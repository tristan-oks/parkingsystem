package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.parkit.parkingsystem.service.Discount;


public class CalculateDiscountTest {

  @Test
  public void calculateDiscountForRecurringUser() {

    Discount discount = new Discount();
    String vehiculeRegNumber = "ABCDEF";
    double discountCalculated = discount.calculate(vehiculeRegNumber);
    assertEquals(discountCalculated, 0.95);
  }

  @Test
  public void calculateDiscountForNonRecurringUser() {

    Discount discount = new Discount();
    String vehiculeRegNumber = "ABCDEF";
    double discountCalculated = discount.calculate(vehiculeRegNumber);
    assertEquals(discountCalculated, 1);
  }


}
