package com.hedvig.claims.web.dto;

import com.hedvig.claims.query.ClaimReportEntity;
import lombok.Value;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

@Value
public class ClaimReportDTO {

  private static String SEK = "SEK";


  private String claimId;
  private String memberId;
  private LocalDate dateOfLoss;
  private LocalDate notificationDate;
  private Year claimYear;
  private String descriptionOfLoss;
  private MonetaryAmount grossPaid;
  private MonetaryAmount reserved;
  private MonetaryAmount totalIncurred;
  private String claimStatus;
  private LocalDate claimStatusLastUpdated;
  private boolean coveringEmployee;

  public static ClaimReportDTO fromClaimReportEntity(ClaimReportEntity e) {
    return new ClaimReportDTO(
      e.getClaimId(),
      e.getMemberId(),
      e.getDateOfLoss(),
      e.getNotificationDate(),
      Year.of(e.getClaimYear() == null ? Year.now().getValue() : e.getClaimYear()),
      e.getDescriptionOfLoss(),
      Money.of(e.getGrossPaid() == null ? BigDecimal.ZERO : e.getGrossPaid(), e.getCurrency() == null ? "SEK" : e.getCurrency()),
      Money.of(e.getReserved() == null ? BigDecimal.ZERO : e.getReserved(), e.getCurrency() == null ? "SEK" : e.getCurrency()),
      Money.of((e.getGrossPaid() == null ? BigDecimal.ZERO : e.getGrossPaid()).subtract(e.getReserved() == null ? BigDecimal.ZERO : e.getReserved()), e.getCurrency() == null ? "SEK" : e.getCurrency()),
      e.getClaimStatus(),
      e.getClaimStatusLastUpdated(),
      e.isCoveringEmployee());
  }
}
