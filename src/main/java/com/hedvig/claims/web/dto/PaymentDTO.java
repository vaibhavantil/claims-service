package com.hedvig.claims.web.dto;

import com.hedvig.claims.aggregates.PayoutStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;

public class PaymentDTO extends HedvigBackofficeDTO {

  public MonetaryAmount amount;
  public MonetaryAmount deductible;
  public String note;
  public LocalDateTime payoutDate;
  public Boolean exGratia;
  public PaymentType type;
  public String handlerReference;
  public UUID transactionId;
  public PayoutStatus status;

  public PaymentDTO(
    String paymentId,
    String claimsId,
    LocalDateTime registrationDate,
    String userId,
    Double amount,
    Double deductible,
    String note,
    LocalDateTime payoutDate,
    Boolean exGratia,
    PaymentType type,
    String handlerReference,
    UUID transactionId,
    PayoutStatus status) {
    this.id = paymentId;
    this.claimID = claimsId;
    this.date = registrationDate;
    this.userId = userId;
    this.amount = Money.of(amount, "SEK");
    this.deductible = Money.of(deductible, "SEK");
    this.note = note;
    this.payoutDate = payoutDate;
    this.exGratia = exGratia;
    this.type = type;
    this.handlerReference = handlerReference;
    this.transactionId = transactionId;
    this.status = status;
  }
}
