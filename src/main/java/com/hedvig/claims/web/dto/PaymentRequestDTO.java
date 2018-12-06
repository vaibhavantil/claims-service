package com.hedvig.claims.web.dto;

import java.util.UUID;
import javax.money.MonetaryAmount;
import lombok.Value;

@Value
public class PaymentRequestDTO {

  UUID claimId;
  String memberId;
  MonetaryAmount amount;
  String handlerReference;
  boolean sanctionCheckSkipped;
  String paymentRequestNote;
  boolean exGratia;
}
