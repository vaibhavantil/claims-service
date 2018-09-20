package com.hedvig.claims.serviceIntegration.paymentService.dto;

import java.time.LocalDate;
import javax.money.MonetaryAmount;
import lombok.Value;

@Value
public class PayoutRequest {
  private MonetaryAmount amount;
  private  String address;
  private  String countryCode;
  private  LocalDate dateOfBirth;
  private  String firstName;
  private  String lastName;
}
