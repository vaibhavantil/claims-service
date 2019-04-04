package com.hedvig.claims.web.dto;

import com.hedvig.claims.query.ClaimReportHistoryEntity;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Value
public class BDXReportClaimHistoryDTO {
  private String claimId;
  private String memberId;
  private BigDecimal grossPaid;
  private BigDecimal reserved;
  private LocalDate dateOfLoss;
  private Instant timeOfKnowledge;


  public static BDXReportClaimHistoryDTO from(ClaimReportHistoryEntity entity) {
    return new BDXReportClaimHistoryDTO(
      entity.getClaimId(),
      entity.getMemberId(),
      entity.getGrossPaid(),
      entity.getReserved(),
      entity.getDateOfLoss(),
      entity.getTimeOfKnowledge()
    );
  }

}
