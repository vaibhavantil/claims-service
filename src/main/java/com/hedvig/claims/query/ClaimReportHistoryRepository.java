package com.hedvig.claims.query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClaimReportHistoryRepository extends JpaRepository<ClaimReportHistoryEntity, UUID> {
  List<ClaimReportHistoryEntity> findByClaimId(String claimId);
}
