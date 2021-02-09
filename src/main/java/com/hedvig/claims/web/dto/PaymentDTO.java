package com.hedvig.claims.web.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hedvig.claims.aggregates.PayoutStatus;
import com.hedvig.claims.util.DoubleOrMonetaryAmountToDoubleDeserializer;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentDTO extends HedvigBackofficeDTO {

  @JsonDeserialize(using = DoubleOrMonetaryAmountToDoubleDeserializer.class)
  @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
  public Double amount;
  @JsonDeserialize(using = DoubleOrMonetaryAmountToDoubleDeserializer.class)
  @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
  public Double deductible;
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
    this.amount = amount;
    this.deductible = deductible;
    this.note = note;
    this.payoutDate = payoutDate;
    this.exGratia = exGratia;
    this.type = type;
    this.handlerReference = handlerReference;
    this.transactionId = transactionId;
    this.status = status;
  }
}
