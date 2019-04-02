package com.hedvig.claims.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ClaimReportHistoryRepository extends JpaRepository<ClaimReportHistoryEntity, UUID> {
  List<ClaimReportHistoryEntity> findByClaimId(String claimId);

  @Query("from ClaimReportHistoryEntity where timeOfKnowledge >= :from AND timeOfKnowledge <= :to")
  List<ClaimReportHistoryEntity> findByTimeOfKnowledge(Instant from, Instant to);
}
