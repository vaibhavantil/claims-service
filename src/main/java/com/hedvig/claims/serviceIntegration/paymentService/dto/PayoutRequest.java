package com.hedvig.claims.serviceIntegration.paymentService.dto;

import com.hedvig.claims.serviceIntegration.memberService.dto.Member;
import java.time.LocalDate;
import javax.money.MonetaryAmount;
import lombok.Value;

@Value
public class PayoutRequest {

  private MonetaryAmount amount;
  private String address;
  private String countryCode;
  private LocalDate dateOfBirth;
  private String firstName;
  private String lastName;

  public PayoutRequest(Member member, MonetaryAmount amount) {
    this.amount = amount;
    this.address = member.getStreet() + " " + member.getCity() + " " + member.getZipCode();
    this.countryCode = member.getCountry();
    this.dateOfBirth = member.getBirthDate();
    this.firstName = member.getFirstName();
    this.lastName = member.getLastName();
  }
}
