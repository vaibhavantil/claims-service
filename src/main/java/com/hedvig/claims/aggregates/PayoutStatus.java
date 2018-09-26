package com.hedvig.claims.aggregates;

import com.hedvig.claims.serviceIntegration.paymentService.dto.TransactionStatus;

public enum PayoutStatus {
  PREPARED,
  INITIATED,
  SANCTION_LIST_HIT,
  COMPLETED,
  FAILED;

  public static PayoutStatus parseToPayoutStatus(TransactionStatus transactionStatus) {
    switch (transactionStatus) {
      case INITIATED:
        return INITIATED;
      case NOT_ACCEPTED:
      case FAILED:
        return FAILED;
      case FORBIDDEN:
        return SANCTION_LIST_HIT;
      case COMPLETED:
        return COMPLETED;
      default:
        return FAILED;
    }
  }
}
