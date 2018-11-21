package com.hedvig.claims.commands;

import com.hedvig.claims.aggregates.ClaimSource;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.time.Instant;
import java.time.LocalDateTime;

@Value
public class CreateBackofficeClaimCommand {
  @TargetAggregateIdentifier
  String id;
  String memberId;
  Instant registrationDate;
  ClaimSource claimSource;
}
